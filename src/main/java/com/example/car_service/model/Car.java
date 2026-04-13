package com.example.car_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String brand;
    private String model;
    private Double price;
    private LocalDate registrationDate;
    private Boolean checkAuto = false;

    @ManyToOne
    @JoinColumn(name = "concessionario_id") // Questa è la Foreign Key fisica nel DB
    private Concessionario concessionario;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<Maintenance> maintenances;

    @Column(name = "car_Request_ID") // <--- Questo dice a Hibernate come chiamare la colonna nel DB
    @JsonProperty("car-Request-ID") // <--- Questo dice a Jackson come chiamare il campo nel JSON
    private String requestId;
}