package com.example.car_service.repository;

import com.example.car_service.model.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    
    List<Maintenance> findByDescriptionIgnoreCaseContaining(String description);


    List<Maintenance> findByDescriptionIgnoreCaseContainingAndCostLessThanEqual(String description, Double cost);

    // Per il costo (Double) usa il confronto esatto
    //List<Maintenance> findByCost(Double cost);

    // OPPURE, se vuoi cercare costi "fino a" una certa cifra:
    List<Maintenance> findByCostLessThanEqual(Double cost);
}