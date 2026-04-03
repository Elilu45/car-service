package com.example.car_service.service;

import com.example.car_service.dto.CarDTO;
import com.example.car_service.model.Car;
import com.example.car_service.repository.CarRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service // <-- FONDAMENTALE: Dice a Spring che questa è la classe da "iniettare"
@Slf4j // <-- Questa annotazione crea automaticamente un oggetto chiamato 'log'
public class CarServiceImpl implements CarService {

    private final CarRepository repository;

    // Dependency Injection tramite costruttore (quello che fanno i tuoi colleghi)
    public CarServiceImpl(CarRepository repository) {
        this.repository = repository;
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

    @Async // <--- DICHIARA CHE IL METODO GIRA SU UN ALTRO THREAD
    @Override
    public void processExternalCheck(Car car) {
        try {
            log.info("Inizio controllo esterno per: " + car.getBrand() + " alle " + LocalTime.now());

            // Simuliamo un'attesa di 5 secondi
            Thread.sleep(5000);

            // 1. Modifichi l'oggetto in memoria
            car.setCheckAuto(true);
            
            // 2. FONDAMENTALE: Salvi di nuovo l'oggetto per aggiornare il DB
            repository.save(car);
            
            log.info("Controllo completato per: " + car.getBrand() + " alle " + LocalTime.now());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

}
