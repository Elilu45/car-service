package com.example.car_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({ "id", "nome", "indirizzo", "citta", "telefono", "dataApertura", "conc-Request-ID" })
public class Concessionario{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String indirizzo;
    private String citta;
    private String telefono;
    private LocalDate dataApertura;

    @Column(name = "conc_Request_ID") // <--- Questo dice a Hibernate come chiamare la colonna nel DB
    @JsonProperty("conc-Request-ID") // <--- Questo dice a Jackson come chiamare il campo nel JSON
    private String requestId;
}