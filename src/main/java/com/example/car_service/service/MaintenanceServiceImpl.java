package com.example.car_service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.car_service.dto.MaintenanceDTO;
import com.example.car_service.mapper.MaintenanceMapper;
import com.example.car_service.model.Car;
import com.example.car_service.model.Maintenance;
import com.example.car_service.repository.CarRepository;
import com.example.car_service.repository.MaintenanceRepository;

@Service // <-- FONDAMENTALE: Dice a Spring che questa è la classe da "iniettare"
@Slf4j // <-- Questa annotazione crea automaticamente un oggetto chiamato 'log'
@RequiredArgsConstructor // <--- Genera il costruttore per tutti i campi "final"
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository repository;
    private final CarRepository carRepository; // Aggiungi questo campo per accedere alle car
    private final MaintenanceMapper maintenanceMapper; // Aggiungi questo campo per usare MapStruct

    @Override
    public MaintenanceDTO saveMaintenance(MaintenanceDTO maintenanceDTO) {
        log.info("Salvataggio manutenzione: {} costo {}", maintenanceDTO.getDescription(), maintenanceDTO.getCost());

        // 1. Usiamo MapStruct per creare l'Entity dal DTO
        Maintenance maintenance = maintenanceMapper.toEntity(maintenanceDTO);

        // 2. RECUPERIAMO LA CAR DAL DB
        // Usiamo il repository delle auto (che devi iniettare con @Autowired)
        Car car = carRepository.findById(Objects.requireNonNull(maintenanceDTO.getCarId(), "ID mancante"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auto non trovata con ID: " + maintenanceDTO.getCarId()));

        maintenance.setCar(car);

        LocalDate oggi = LocalDate.now();
        maintenance.setInterventionDate(oggi);

        String currentRequestId = MDC.get("x-Request-ID");
        car.setRequestId(currentRequestId);

        // Salva l'Entity
        Maintenance maintenanceSalvata = repository.save(maintenance);
        log.info("Manutenzione salvata: {} costo {}", maintenanceSalvata.getDescription(), maintenanceSalvata.getCost());

        // 3. ENTITY -> DTO (Conversione per la Risposta)
        return maintenanceMapper.toDTO(maintenanceSalvata);
    }


    @Override
    public void processExternalCheck(MaintenanceDTO maintenanceDTO, String requestId) {
        try {
            // Fondamentale: iniettiamo l'ID nel nuovo thread
            MDC.put("x-Request-ID", requestId);

            log.info("Inizio manutenzione esterna per intervento: {} con costo {} il {}" , maintenanceDTO.getDescription(), maintenanceDTO.getCost(), LocalDate.now());

            // Simuliamo un'attesa di 5 secondi
            Thread.sleep(5000);
            
            log.info("Controllo completato perintervento: {} con costo {} il {}", maintenanceDTO.getDescription(), maintenanceDTO.getCost(), LocalDate.now());

        } catch (InterruptedException e) {
            log.error("Errore durante il controllo asincrono", e);
        } finally {
            // Puliamo l'MDC anche qui per buona norma
            MDC.remove("x-Request-ID");
        }
    }

    @Override
    public List<MaintenanceDTO> searchMaintenances(String description, Double cost) {
        
        List<Maintenance> entities;

        // 1. Cerchiamo le Entity (Mondo Interno)
        if (description != null && cost != null) {
            entities = repository.findByDescriptionIgnoreCaseContainingAndCostLessThanEqual(description, cost);
        } else if (description != null) {
            entities = repository.findByDescriptionIgnoreCaseContaining(description);
        } else if (cost != null) {
            entities = repository.findByCostLessThanEqual(cost);
        } else {
            entities = repository.findAll();
        }

        // 2. Trasformiamo la lista di Entity in una lista di DTO (Mondo Esterno)
        return maintenanceMapper.toDTOList(entities);
    }
    
}
