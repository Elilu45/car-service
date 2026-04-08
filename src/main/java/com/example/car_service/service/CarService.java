package com.example.car_service.service;

import com.example.car_service.dto.CarDTO;
import java.util.List;

public interface CarService {
    // Promessa 1: "Ti darò una lista di auto filtrate"
    List<CarDTO> searchCars(String brand, String model);

    // Promessa 2: "Salverò un'auto partendo da un DTO"
    CarDTO saveCar(CarDTO carDTO);

    void processExternalCheck(CarDTO carDTO, String requestId); // Promessa 3: "Farò un controllo esterno (simulato) su un'auto"
}
