package com.example.car_service.controller;

import com.example.car_service.dto.CustomResponse;
import com.example.car_service.model.Car;
import com.example.car_service.repository.CarRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarRepository repository;

    public CarController(CarRepository repository) {
        this.repository = repository;
    }

    // @PostMapping
    // public Car create(@RequestBody Car car) {
    //     return repository.save(car);
    // }

    @PostMapping
    public ResponseEntity<CustomResponse<Car>> create(@RequestBody Car car) {
        Car savedCar = repository.save(car);

        // Creiamo il nostro oggetto risposta personalizzato
        CustomResponse<Car> response = new CustomResponse<>(
   "Ottimo! L'auto è stata salvata nel database.", 
            savedCar
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Car> getByBrand(@RequestParam(required = false) String brand) {
        if (brand == null) {
            return repository.findAll(); // Se non c'è il brand, dammi tutto!
        }
        return repository.findByBrandIgnoreCase(brand);
    }
}