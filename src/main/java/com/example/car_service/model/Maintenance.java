package com.example.car_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private LocalDate interventionDate;
    private Double cost;
    
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;
    
    @Column(name = "maintenance_Request_ID") // <--- Questo dice a Hibernate come chiamare la colonna nel DB
    @JsonProperty("maintenance-Request-ID") // <--- Questo dice a Jackson come chiamare il campo nel JSON
    private String requestId;
}
