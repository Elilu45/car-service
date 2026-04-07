package com.example.car_service.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull; // In alternativa usa questo

import org.slf4j.MDC; // <--- Per la gestione del contesto dei log
import org.springframework.stereotype.Component;

import java.util.UUID; // <--- Per generare l'ID univoco

@Component  // <--- AGGIUNGI QUESTA RIGA! Dice a Spring: "Usa questa classe come filtro"
public class LoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "car-Request-ID";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        // 1. Generiamo un ID univoco (UUID)
        String requestId = UUID.randomUUID().toString();

        // 2. Lo mettiamo nell'MDC (così finirà nei log)
        MDC.put("x-Request-ID", requestId);

        // 3. Lo mettiamo nella RESPONSE (come richiesto dal collega)
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 4. IMPORTANTISSIMO: Puliamo l'MDC alla fine della richiesta
            MDC.remove("x-Request-ID");
        }
    }
    
}
