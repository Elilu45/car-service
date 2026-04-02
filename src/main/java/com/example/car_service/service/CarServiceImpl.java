package com.example.car_service.service;

import com.example.car_service.dto.CarDTO;
import com.example.car_service.model.Car;
import com.example.car_service.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service // <-- FONDAMENTALE: Dice a Spring che questa è la classe da "iniettare"
public class CarServiceImpl implements CarService {

    private final CarRepository repository;

    // Dependency Injection tramite costruttore (quello che fanno i tuoi colleghi)
    public CarServiceImpl(CarRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Car> searchCars(String brand, String model) {
        // Spostiamo qui la logica degli IF che avevi nel Controller
        if (brand != null && model != null) {
            return repository.findByBrandIgnoreCaseContainingAndModelIgnoreCaseContaining(brand, model);
        } else if (brand != null) {
            return repository.findByBrandIgnoreCaseContaining(brand);
        } else if (model != null) {
            return repository.findByModelIgnoreCaseContaining(model);
        } else {
            return repository.findAll();
        }
    }

    @Override
    public Car saveCar(CarDTO carDTO) {
        Car car = new Car();
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setPrice(carDTO.getPrice());
        
        car.setRegistrationDate(LocalDate.now()); // Impostiamo la data di registrazione a oggi

        return repository.save(car);
    }

}
