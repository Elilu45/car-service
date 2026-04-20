package com.example.car_service.controller;

import com.example.car_service.dto.CarDTO;
import com.example.car_service.dto.CustomResponse;
import com.example.car_service.service.CarService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC; // <--- Per la gestione del contesto dei log

@RestController
@RequestMapping("/api/cars")
@Slf4j
public class CarController {

    private final CarService carService; // Usiamo l'interfaccia!

    public CarController(CarService carService) {
        this.carService = carService;
    }


    @PostMapping
    public ResponseEntity<CustomResponse<CarDTO>> create(@Valid @RequestBody CarDTO carDTO) {
        // Trasformiamo il DTO in una Entity per salvarla nel DB
        // 1. Recuperiamo l'ID che il Filtro ha messo nell'MDC
        String currentRequestId = MDC.get("x-Request-ID");

        CarDTO savedCarDto = carService.saveCar(carDTO); // Ora riceve un DTO
        log.info("Auto salvata con ID {}. Lancio controllo asincrono...", savedCarDto.getId());

        // Chiamata asincrona: il codice NON si ferma qui ad aspettare 5 secondi!
        carService.processExternalCheck(savedCarDto, currentRequestId);

        // Creiamo il nostro oggetto risposta personalizzato
        CustomResponse<CarDTO> response = new CustomResponse<>(
   "Ottimo! L'auto è stata salvata nel database.", 
            savedCarDto
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CustomResponse<List<CarDTO>>> getByBrand(
        @RequestParam(required = false) String brand,
        @RequestParam(required = false) String model
    ) {
        log.info("Ricevuta richiesta di recupero di tutte le auto");

        List<CarDTO> cars = carService.searchCars(brand, model); // Il tuo service restituisce la lista filtrata   

        log.info("Recuperati {} auto dal database", cars.size());

        // Creiamo un messaggio dinamico per aiutare chi legge i log
        String message = cars.isEmpty() ? "Nessuna auto trovata" : "Lista auto recuperata con successo";

        return ResponseEntity.ok(new CustomResponse<>(message, cars));
        
    }


    @GetMapping("/{targa}/check-washing")
    public ResponseEntity<String> checkCarWashing(@PathVariable String targa) {
        // Chiamiamo il Service che si occuperà della logica
        String status = carService.checkWashingStatus(targa);
        return ResponseEntity.ok("Stato lavaggio per auto " + targa + ": " + status);
    }
}