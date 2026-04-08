package com.example.car_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.car_service.dto.ConcessionarioDTO;
import com.example.car_service.dto.CustomResponse;
import com.example.car_service.model.Concessionario;
import com.example.car_service.service.ConcessionarioService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.slf4j.MDC; // <--- Per la gestione del contesto dei log


@RestController
@RequestMapping("/api/concessionari")
@Slf4j
public class ConcessionarioController {

    private final ConcessionarioService concessionarioService; // Usiamo l'interfaccia!

    public ConcessionarioController(ConcessionarioService concessionarioService) {
        this.concessionarioService = concessionarioService;
    }
    
    @PostMapping
    public ResponseEntity<CustomResponse<ConcessionarioDTO>> create(@Valid @RequestBody ConcessionarioDTO concessionarioDTO) {
                // Trasformiamo il DTO in una Entity per salvarla nel DB
        // 1. Recuperiamo l'ID che il Filtro ha messo nell'MDC
        String currentRequestId = MDC.get("x-Request-ID");

        ConcessionarioDTO savedConcessionarioDto = concessionarioService.saveConcessionario(concessionarioDTO); // Il service si occupa di tutto, anche di mettere la data!

        log.info("Concessionario salvato con ID {}. Lancio controllo asincrono...", savedConcessionarioDto.getId());

        // Chiamata asincrona: il codice NON si ferma qui ad aspettare 5 secondi!
        concessionarioService.processExternalCheck(savedConcessionarioDto, currentRequestId);

        // Creiamo il nostro oggetto risposta personalizzato
        CustomResponse<ConcessionarioDTO> response = new CustomResponse<>(
   "Ottimo! Il concessionario è stato salvato nel database.", 
            savedConcessionarioDto
        );
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CustomResponse<List<ConcessionarioDTO>>> getByBrand(
        @RequestParam(required = false) String nome,
        @RequestParam(required = false) String citta
    ) {
        log.info("Ricevuta richiesta di recupero di tutti i concessionari");

        List<ConcessionarioDTO> concessionari = concessionarioService.searchConcessionari(nome, citta); // Il tuo service restituisce la lista filtrata   

        log.info("Recuperati {} concessionari dal database", concessionari.size());

        // Creiamo un messaggio dinamico per aiutare chi legge i log
        String message = concessionari.isEmpty() ? "Nessun concessionario trovato" : "Lista concessionari recuperata con successo";

        return ResponseEntity.ok(new CustomResponse<>(message, concessionari));
    }
    
}
