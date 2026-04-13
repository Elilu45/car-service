package com.example.car_service.service;

import java.util.List;

import com.example.car_service.dto.MaintenanceDTO;

public interface MaintenanceService {
        // Promessa 1: "Ti darò una lista di auto filtrate"
    List<MaintenanceDTO> searchMaintenances(String description, Double cost);

    // Promessa 2: "Salverò un'auto partendo da un DTO"
    MaintenanceDTO saveMaintenance(MaintenanceDTO maintenanceDTO);

    void processExternalCheck(MaintenanceDTO maintenanceDTO, String requestId); // Promessa 3: "Farò un controllo esterno (simulato) su una manutenzione"
}
