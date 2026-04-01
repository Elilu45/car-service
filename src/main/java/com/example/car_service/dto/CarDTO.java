package com.example.car_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data; // Importa l'annotazione magica

@Data // Questa annotazione genera automaticamente: Getter, Setter, toString, equals e hashCode
public class CarDTO {
    
    @NotBlank(message = "La marca non può essere vuota")
    private String brand;

    @NotBlank(message = "Il modello non può essere vuoto")
    private String model;
}