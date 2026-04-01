package com.example.car_service.dto;

//import java.time.LocalDate;

//import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data; // Importa l'annotazione magica

@Data // Questa annotazione genera automaticamente: Getter, Setter, toString, equals e hashCode
public class CarDTO {
    
    @NotBlank(message = "La marca non può essere vuota")
    private String brand;

    @NotBlank(message = "Il modello non può essere vuoto")
    private String model;

    @NotNull(message = "Il prezzo non può essere vuoto")
    @Positive(message = "Il prezzo deve essere maggiore di zero")
    private Double price;

    // @NotNull(message = "La data di registrazione non può essere vuota")
    // @PastOrPresent(message = "La data non può essere nel futuro")
    // @JsonFormat(pattern = "yyyy-MM-dd")
    // private LocalDate registrationDate;
}