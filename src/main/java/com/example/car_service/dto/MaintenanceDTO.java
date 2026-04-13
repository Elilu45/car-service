package com.example.car_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

import lombok.Data;


@Data
public class MaintenanceDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "La descrizione della manutenzione non può essere vuota")
    private String description;

    private LocalDate interventionDate;

    @NotNull(message = "Il costo della manutenzione non può essere vuoto")
    @Positive(message = "Il costo deve essere maggiore di zero")
    private Double cost;

    @NotNull(message = "L'ID dell'auto non può essere vuoto")
    private Long carId;

}