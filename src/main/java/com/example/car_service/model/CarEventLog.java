package com.example.car_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;




@Entity
@Data
@Table(name = "car_event_logs")
@NoArgsConstructor
@AllArgsConstructor
public class CarEventLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String payload;
    private LocalDateTime receivedAt;
    
}
