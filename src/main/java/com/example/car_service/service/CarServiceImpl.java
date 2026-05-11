package com.example.car_service.service;

import com.example.car_service.dto.CarDTO;
import com.example.car_service.exception.ResourceNotFoundException;
import com.example.car_service.model.Car;
import com.example.car_service.model.Concessionario;
import com.example.car_service.repository.CarRepository;
import com.example.car_service.repository.ConcessionarioRepository;
import com.example.car_service.mapper.CarMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC; // <--- Per la gestione del contesto dei log
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
//import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service // <-- FONDAMENTALE: Dice a Spring che questa è la classe da "iniettare"
@Slf4j // <-- Questa annotazione crea automaticamente un oggetto chiamato 'log'
@RequiredArgsConstructor // <--- Genera il costruttore per tutti i campi "final"
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final ConcessionarioRepository concessionarioRepository; // Aggiungi questo campo per accedere ai concessionari
    private final CarMapper carMapper; // Aggiungi questo campo per usare MapStruct
    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate; // Iniettiamo il template di Kafka

    //Definiamo l'URL del tuo Mock
    @Value("${external.washing.url}")
    private String washingUrl;

    @Override
    public CarDTO saveCar(CarDTO carDTO) {
        log.info("Salvataggio auto: {} modello {}", carDTO.getBrand(), carDTO.getModel());

        // Normalizzazione: rendiamo tutto maiuscolo e togliamo eventuali spazi ai lati
        String targaPulita = carDTO.getTarga().toUpperCase().trim();
        carDTO.setTarga(targaPulita);

        // 1. Usiamo MapStruct per creare l'Entity dal DTO
        Car car = carMapper.toEntity(carDTO);

        // 2. RECUPERIAMO IL CONCESSIONARIO DAL DB
        // Usiamo il repository dei concessionari (che devi iniettare con @Autowired)
        Concessionario concessionario = concessionarioRepository.findById(Objects.requireNonNull(carDTO.getConcessionarioId(), "ID mancante"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Concessionario non trovato con ID: " + carDTO.getConcessionarioId()));

        car.setConcessionario(concessionario);

        LocalDate oggi = LocalDate.now();
        car.setRegistrationDate(oggi); // Impostiamo la data di registrazione a oggi

        String currentRequestId = MDC.get("x-Request-ID");
        car.setRequestId(currentRequestId);

        // Salva l'Entity
        Car carSalvata = carRepository.save(car);
        log.info("Auto salvata: {} modello {} il {}", carSalvata.getBrand(), carSalvata.getModel(), oggi);

        // 3. ENTITY -> DTO (Conversione per la Risposta)
        CarDTO savedDto = carMapper.toDTO(carSalvata);

        // 4. NUOVO: Invio messaggio a Kafka
        try {
            String message = "Creata nuova auto con targa: " + savedDto.getTarga();
            kafkaTemplate.send("car-topic", message);
            log.info("Messaggio inviato al topic car-topic");
        } catch (Exception e) {
            log.error("Errore invio Kafka", e);
        }

        return savedDto;
    }

    @Async // <--- DICHIARA CHE IL METODO GIRA SU UN ALTRO THREAD
    @Override
    public void processExternalCheck(CarDTO carDTO, String requestId) {
        try {
            // Fondamentale: iniettiamo l'ID nel nuovo thread
            MDC.put("x-Request-ID", requestId);

            log.info("Inizio controllo esterno per: {} modello {} il {}" , carDTO.getBrand(), carDTO.getModel(), LocalDate.now());

            // Simuliamo un'attesa di 5 secondi
            Thread.sleep(5000);

            // 1. RECUPERA l'Entity aggiornata dal database usando l'ID del DTO
            // Usiamo l'ID che abbiamo aggiunto al DTO prima!
            Car car = carRepository.findById(Objects.requireNonNull(carDTO.getId(), "ID mancante"))
                    .orElseThrow(() -> new RuntimeException("Auto non trovata durante il controllo asincrono"));

            // 2. MODIFICA l'Entity (non il DTO)
            car.setCheckAuto(true);
            
            // 3. SALVA l'Entity
            carRepository.save(car);
            
            log.info("Controllo completato per: {} modello {} il {}", car.getBrand(), car.getModel(), LocalDate.now());

        } catch (InterruptedException e) {
            log.error("Errore durante il controllo asincrono", e);
        } finally {
            // Puliamo l'MDC anche qui per buona norma
            MDC.remove("x-Request-ID");
        }
    }

    @Override
    public List<CarDTO> searchCars(String brand, String model) {
        List<Car> entities;

        // 1. Cerchiamo le Entity (Mondo Interno)
        if (brand != null && model != null) {
            entities = carRepository.findByBrandIgnoreCaseContainingAndModelIgnoreCaseContaining(brand, model);
        } else if (brand != null) {
            entities = carRepository.findByBrandIgnoreCaseContaining(brand);
        } else if (model != null) {
            entities = carRepository.findByModelIgnoreCaseContaining(model);
        } else {
            entities = carRepository.findAll();
        }
        // 2. Trasformiamo la lista di Entity in una lista di DTO (Mondo Esterno)
        return carMapper.toDTOList(entities);
    }


    public String checkWashingStatus(String targa) {
        // 1. VALIDAZIONE: L'auto esiste nel mio sistema?
        // Usiamo il repository che abbiamo già per cercare l'auto
        boolean exists = carRepository.existsByTargaIgnoreCase(targa); 
        
        if (!exists) {
            // Se non esiste, ci fermiamo subito. Inutile chiamare WireMock!
            log.warn("Tentativo di check lavaggio per targa inesistente: {}", targa);
            throw new ResourceNotFoundException("Auto con targa " + targa + " non trovata");
        }

        // 2. CHIAMATA ESTERNA: Solo se l'auto esiste, facciamo la chiamata a WireMock
        // Facciamo la chiamata GET
        // RestTemplate.getForObject prende l'URL e il tipo di risposta che ci aspettiamo (String)
        try {
            log.info("L'auto {} esiste. Procedo al controllo lavaggio esterno...", targa);
            return restTemplate.getForObject(Objects.requireNonNull(washingUrl), String.class);
        } catch (Exception e) {
            log.error("Errore comunicazione con WireMock: {}", e.getMessage());
            return "Servizio lavaggio non raggiungibile!";
        }
    }
}