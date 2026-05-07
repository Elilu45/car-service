package com.example.car_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.car_service.model.CarEventLog;

public interface CarEventLogRepository extends JpaRepository<CarEventLog, Long> {



}