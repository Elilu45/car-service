package com.example.car_service.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

//import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data; // Importa l'annotazione magica

@Data // Questa annotazione genera automaticamente: Getter, Setter, toString, equals e hashCode
public class CarDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id; 
    
    @NotBlank(message = "La marca non può essere vuota")
    private String brand;

    @NotBlank(message = "Il modello non può essere vuoto")
    private String model;

    @NotNull(message = "Il prezzo non può essere vuoto")
    @Positive(message = "Il prezzo deve essere maggiore di zero")
    private Double price;

    @NotBlank(message = "La targa non può essere vuota")
    @Size(min = 7, max = 7, message = "La targa deve essere di esattamente 7 caratteri")
    @Pattern(
        regexp = "^[A-Z]{2}[0-9]{3}[A-Z]{2}$", 
        message = "Formato targa non valido (esempio: AB123CD)"
    )
    private String targa;

    @NotNull(message = "L'ID del concessionario non può essere vuoto")
    private Long concessionarioId;

    // @NotNull(message = "La data di registrazione non può essere vuota")
    // @PastOrPresent(message = "La data non può essere nel futuro")
    // @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate registrationDate;

    private Boolean checkAuto;
}