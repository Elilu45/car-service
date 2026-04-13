package com.example.car_service.controller;


import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.car_service.dto.MaintenanceDTO;
import com.example.car_service.dto.CustomResponse;
import com.example.car_service.service.MaintenanceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/maintenances")
@Slf4j
public class MaintenanceController {

    private final MaintenanceService maintenanceService; // Usiamo l'interfaccia!

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @PostMapping("/create")
    public ResponseEntity<CustomResponse<MaintenanceDTO>> create(@Valid @RequestBody MaintenanceDTO maintenanceDTO) {
        // Trasformiamo il DTO in una Entity per salvarla nel DB
        // 1. Recuperiamo l'ID che il Filtro ha messo nell'MDC
        String currentRequestId = MDC.get("x-Request-ID");

        MaintenanceDTO savedMaintenanceDto = maintenanceService.saveMaintenance(maintenanceDTO); // Ora riceve un DTO
        log.info("Manutenzione salvata con ID {}. Lancio controllo asincrono...", savedMaintenanceDto.getId());

        // Chiamata asincrona: il codice NON si ferma qui ad aspettare 5 secondi!
        maintenanceService.processExternalCheck(savedMaintenanceDto, currentRequestId);

                // Creiamo il nostro oggetto risposta personalizzato
        CustomResponse<MaintenanceDTO> response = new CustomResponse<>(
   "Ottimo! La manutenzione è stata salvata nel database.", 
            savedMaintenanceDto
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }
    
    
    @GetMapping("/search")
    public ResponseEntity<CustomResponse<List<MaintenanceDTO>>> getByDescriptionCost(
        @RequestParam(required = false) String description,
        @RequestParam(required = false) Double cost
    ) {
        log.info("Ricevuta richiesta di recupero di tutte le manutenzioni");

        List<MaintenanceDTO> maintenances = maintenanceService.searchMaintenances(description, cost); // Il tuo service restituisce la lista filtrata   

        log.info("Recuperate {} manutenzioni dal database", maintenances.size());
        // Creiamo un messaggio dinamico per aiutare chi legge i log
        String message = maintenances.isEmpty() ? "Nessuna manutenzione trovata" : "Lista manutenzioni recuperata con successo";

        return ResponseEntity.ok(new CustomResponse<>(message, maintenances));
        
    }
}
