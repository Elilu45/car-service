package com.example.car_service.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;


@Data
@NoArgsConstructor // Genera il costruttore vuoto necessario a Jackson
public class CustomResponse<T> {
    private String message;
    private T data; // T permette di metterci dentro un'auto, una lista, o altro

    @JsonProperty("car-Request-ID") // <--- Cambia il nome solo nel JSON finale
    private String requestId;

    public CustomResponse(String message, T data) {
        this.message = message;
        this.data = data;
        // Recuperiamo l'ID dall'MDC (dove lo ha messo il Filtro)
        this.requestId = org.slf4j.MDC.get("car-Request-ID");
    }
}