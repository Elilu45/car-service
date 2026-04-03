package com.example.car_service.service;

import com.example.car_service.dto.CarDTO;
import com.example.car_service.model.Car;
import java.util.List;

public interface CarService {
    // Promessa 1: "Ti darò una lista di auto filtrate"
    List<Car> searchCars(String brand, String model);

    // Promessa 2: "Salverò un'auto partendo da un DTO"
    Car saveCar(CarDTO carDTO);

    void processExternalCheck(Car car, String requestId); // Promessa 3: "Farò un controllo esterno (simulato) su un'auto"
}
