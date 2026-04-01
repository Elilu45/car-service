package com.example.car_service.controller;

import com.example.car_service.dto.CarDTO;
import com.example.car_service.dto.CustomResponse;
import com.example.car_service.model.Car;
import com.example.car_service.repository.CarRepository;

import jakarta.validation.Valid;

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
    public ResponseEntity<CustomResponse<Car>> create(@Valid @RequestBody CarDTO carDTO) {
        // Trasformiamo il DTO in una Entity per salvarla nel DB
        Car car = new Car();
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setPrice(carDTO.getPrice());
        //car.setRegistrationDate(carDTO.getRegistrationDate());

        Car savedCar = repository.save(car); // Il tuo service salva la Entity

        // Creiamo il nostro oggetto risposta personalizzato
        CustomResponse<Car> response = new CustomResponse<>(
   "Ottimo! L'auto è stata salvata nel database.", 
            savedCar
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // @GetMapping
    // public List<Car> getByBrand(@RequestParam(required = false) String brand) {
    //     if (brand == null) {
    //         return repository.findAll(); // Se non c'è il brand, dammi tutto!
    //     }
    //     return repository.findByBrandIgnoreCase(brand);
    // }

    @GetMapping
    public ResponseEntity<CustomResponse<List<Car>>> getByBrand(
        @RequestParam(required = false) String brand,
        @RequestParam(required = false) String model
    ) {
        List<Car> cars;

        if (brand != null && model != null) {
            cars = repository.findByBrandIgnoreCaseAndModelIgnoreCaseContaining(brand, model);
        } else if (brand != null) {
            cars = repository.findByBrandIgnoreCaseContaining(brand);
        } else if (model != null) {
            cars = repository.findByModelIgnoreCaseContaining(model);
        } else {
            cars = repository.findAll();
        }

        // Creiamo un messaggio dinamico per aiutare chi legge i log
        String message = cars.isEmpty() ? "Nessuna auto trovata" : "Lista auto recuperata con successo";

        return ResponseEntity.ok(new CustomResponse<>(message, cars));
    }
}