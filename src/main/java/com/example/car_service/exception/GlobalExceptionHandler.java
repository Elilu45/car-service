package com.example.car_service.exception;

import com.example.car_service.dto.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        
        // 1. Creiamo una mappa per contenere i dettagli degli errori (es: "brand" -> "non può essere vuoto")
        Map<String, String> errors = new HashMap<>();

        // 2. Cicliamo su tutti gli errori trovati da Spring e riempiamo la mappa
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );

        // 3. Prepariamo la TUA CustomResponse usando la mappa degli errori come "data"
        CustomResponse<Map<String, String>> response = new CustomResponse<>(
            "Errore di validazione: controlla i campi inseriti",
            errors
        );

        // 4. Restituiamo la risposta con lo status 400 (Bad Request)
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse<String>> handleAllExceptions(Exception ex) {
        CustomResponse<String> response = new CustomResponse<>(
            "Si è verificato un errore interno al server",
            ex.getMessage() // O un messaggio più generico per sicurezza
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
