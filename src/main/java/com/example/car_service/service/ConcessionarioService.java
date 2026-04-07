package com.example.car_service.service;

import java.util.List;

import com.example.car_service.dto.ConcessionarioDTO;
import com.example.car_service.model.Concessionario;


public interface ConcessionarioService {
        // Promessa 1: "Ti darò una lista di concessionari filtrati"
    List<Concessionario> searchConcessionari(String nome, String citta);

    // Promessa 2: "Salverò un concessionario partendo da un DTO"
    Concessionario saveConcessionario(ConcessionarioDTO concessionarioDTO);

    void processExternalCheck(Concessionario concessionario, String requestId); // Promessa 3: "Farò un controllo esterno (simulato) su un concessionario"
}
