package com.example.car_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

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
    @Column(updatable = false) // La data non deve cambiare se modifichi l'auto in futuro
    private LocalDate registrationDate;

    // Questo metodo viene eseguito AUTOMATICAMENTE prima del salvataggio nel DB
    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDate.now();
    }
}