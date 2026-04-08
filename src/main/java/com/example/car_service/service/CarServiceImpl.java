package com.example.car_service.service;

import com.example.car_service.dto.CarDTO;
import com.example.car_service.model.Car;
import com.example.car_service.model.Concessionario;
import com.example.car_service.repository.CarRepository;
import com.example.car_service.repository.ConcessionarioRepository;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC; // <--- Per la gestione del contesto dei log
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
//import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service // <-- FONDAMENTALE: Dice a Spring che questa è la classe da "iniettare"
@Slf4j // <-- Questa annotazione crea automaticamente un oggetto chiamato 'log'
public class CarServiceImpl implements CarService {

    private final CarRepository repository;
    private final ConcessionarioRepository concessionarioRepository; // Aggiungi questo


    // Dependency Injection tramite costruttore (quello che fanno i tuoi colleghi)
    public CarServiceImpl(CarRepository repository, ConcessionarioRepository concessionarioRepository) {
        this.repository = repository;
        this.concessionarioRepository = concessionarioRepository;
    }


    @Override
    public CarDTO saveCar(CarDTO carDTO) {
        log.info("Salvataggio auto: {} modello {}", carDTO.getBrand(), carDTO.getModel());

        Car car = new Car();
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setPrice(carDTO.getPrice());

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
        Car carSalvata = repository.save(car);
        log.info("Auto salvata: {} modello {} il {}", carSalvata.getBrand(), carSalvata.getModel(), oggi);

        // 3. ENTITY -> DTO (Conversione per la Risposta)
        return mapToDTO(carSalvata);
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
            Car car = repository.findById(Objects.requireNonNull(carDTO.getId(), "ID mancante"))
                    .orElseThrow(() -> new RuntimeException("Auto non trovata durante il controllo asincrono"));

            // 2. MODIFICA l'Entity (non il DTO)
            car.setCheckAuto(true);
            
            // 3. SALVA l'Entity
            repository.save(car);
            
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
            entities = repository.findByBrandIgnoreCaseContainingAndModelIgnoreCaseContaining(brand, model);
        } else if (brand != null) {
            entities = repository.findByBrandIgnoreCaseContaining(brand);
        } else if (model != null) {
            entities = repository.findByModelIgnoreCaseContaining(model);
        } else {
            entities = repository.findAll();
        }

        // 2. Trasformiamo la lista di Entity in una lista di DTO (Mondo Esterno)
        return entities.stream()
                .map(this::mapToDTO) // Usa il metodo di supporto per convertire ogni Entity in DTO
                .collect(Collectors.toList());
    }


    // METODO DI SUPPORTO (Da aggiungere in fondo alla classe)
    private CarDTO mapToDTO(Car car) {
        CarDTO dto = new CarDTO();
        dto.setId(car.getId());
        dto.setBrand(car.getBrand());
        dto.setModel(car.getModel());
        dto.setPrice(car.getPrice());
        dto.setRegistrationDate(car.getRegistrationDate());
        dto.setCheckAuto(car.getCheckAuto()); // Fondamentale per non avere null!
        
        // Mappiamo solo l'ID del concessionario per rompere la circolarità
        if (car.getConcessionario() != null) {
            dto.setConcessionarioId(car.getConcessionario().getId());
        }
        
        return dto;
    }

}
