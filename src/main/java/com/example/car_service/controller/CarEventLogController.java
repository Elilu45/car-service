package com.example.car_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.car_service.dto.CarEventLogDTO;
import com.example.car_service.dto.CustomResponse;
import com.example.car_service.service.CarEventLogService;


@RestController
@RequestMapping("/api/event/logs")
@Slf4j
@RequiredArgsConstructor
public class CarEventLogController {

    private final CarEventLogService carEventLogService; // Usiamo l'interfaccia!

    @GetMapping
    public ResponseEntity<CustomResponse<List<CarEventLogDTO>>> getAllEventLogs() {
        log.info("Ricevuta richiesta di recupero di tutti gli event log");

        List<CarEventLogDTO> carsEvent = carEventLogService.getAllLogs(); // Il tuo service restituisce la lista filtrata   

        log.info("Recuperati {} event dal database", carsEvent.size());

        // Creiamo un messaggio dinamico per aiutare chi legge i log
        String message = carsEvent.isEmpty() ? "Nessuna event trovato" : "Lista event recuperata con successo";

        return ResponseEntity.ok(new CustomResponse<>(message, carsEvent));
    }
}