package com.example.car_service.controller;

import com.example.car_service.dto.CarDTO;
import com.example.car_service.dto.CustomResponse;
import com.example.car_service.model.Car;
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
    public ResponseEntity<CustomResponse<Car>> create(@Valid @RequestBody CarDTO carDTO) {
        // Trasformiamo il DTO in una Entity per salvarla nel DB
        // 1. Recuperiamo l'ID che il Filtro ha messo nell'MDC
        String currentRequestId = MDC.get("car-Request-ID");

        Car savedCar = carService.saveCar(carDTO); // Il service si occupa di tutto, anche di mettere la data!

        log.info("Auto salvata con ID {}. Lancio controllo asincrono...", savedCar.getId());

        // Chiamata asincrona: il codice NON si ferma qui ad aspettare 5 secondi!
        carService.processExternalCheck(savedCar, currentRequestId);

        // Creiamo il nostro oggetto risposta personalizzato
        CustomResponse<Car> response = new CustomResponse<>(
   "Ottimo! L'auto è stata salvata nel database.", 
            savedCar
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CustomResponse<List<Car>>> getByBrand(
        @RequestParam(required = false) String brand,
        @RequestParam(required = false) String model
    ) {
        List<Car> cars = carService.searchCars(brand, model); // Il tuo service restituisce la lista filtrata   

        // Creiamo un messaggio dinamico per aiutare chi legge i log
        String message = cars.isEmpty() ? "Nessuna auto trovata" : "Lista auto recuperata con successo";

        return ResponseEntity.ok(new CustomResponse<>(message, cars));
    }
}