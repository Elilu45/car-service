package com.example.car_service.repository;

import com.example.car_service.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByBrandIgnoreCase(String brand);
}
