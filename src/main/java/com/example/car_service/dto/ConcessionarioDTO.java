package com.example.car_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data; 

@Data
public class ConcessionarioDTO {
    
    @NotBlank(message = "Il nome non può essere vuoto")
    private String nome;
    @NotBlank(message = "L'indirizzo non può essere vuoto")
    private String indirizzo;
    @NotBlank(message = "La città non può essere vuota")
    private String citta;
    @NotBlank(message = "Il telefono non può essere vuoto")
    @Size(min = 10, max = 10, message = "Il numero di telefono deve essere di esattamente 10 cifre")
    @Pattern(regexp = "^[0-9]*$", message = "Il numero di telefono deve contenere solo cifre")
    //@Pattern(regexp = "^\\d{10}$", message = "Il telefono deve essere composto da esattamente 10 numeri")
    private String telefono;
}
