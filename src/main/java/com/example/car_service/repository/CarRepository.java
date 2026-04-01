package com.example.car_service.repository;

import com.example.car_service.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByBrandIgnoreCaseContainingAndModelIgnoreCaseContaining(String brand, String model);

    List<Car> findByBrandIgnoreCase(String brand);

    List<Car> findByModelIgnoreCase(String model);

    List<Car> findByBrandIgnoreCaseContaining(String brand);

    List<Car> findByModelIgnoreCaseContaining(String model);
}
