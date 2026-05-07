package com.example.car_service.consumer;

import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.example.car_service.model.CarEventLog;
import com.example.car_service.repository.CarEventLogRepository;

import lombok.*;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class CarConsumer {

    private final CarEventLogRepository logRepository;

    @KafkaListener(topics = "car-topic", groupId = "car-group")
    public void listen(String message) {
        log.info("Messaggio ricevuto da Kafka: {}", message);
        
        // Persistenza sulla tabella di log
        CarEventLog eventLog = new CarEventLog();
        eventLog.setPayload(message);
        eventLog.setReceivedAt(LocalDateTime.now());
        
        logRepository.save(eventLog);
        log.info("Evento salvato su DB correttamente!");
    }

}
