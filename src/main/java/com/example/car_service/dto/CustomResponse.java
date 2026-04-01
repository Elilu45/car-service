package com.example.car_service.dto;

public class CustomResponse<T> {
    private String message;
    private T data; // T permette di metterci dentro un'auto, una lista, o altro

    public CustomResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    // Aggiungi Getter e Setter (fondamentali per Spring!)
    public String getMessage() { return message; }
    public T getData() { return data; }
}