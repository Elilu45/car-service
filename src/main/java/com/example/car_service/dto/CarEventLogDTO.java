package com.example.car_service.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Data; // Importa l'annotazione magica

@Data // Questa annotazione genera automaticamente: Getter, Setter, toString, equals e hashCode
public class CarEventLogDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id; 

    @NotBlank(message = "Il payload non può essere vuoto")
    private String payload;

    private LocalDate receivedAt;
}
