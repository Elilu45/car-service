package com.example.car_service.service;

import java.util.List;

import com.example.car_service.dto.ConcessionarioDTO;


public interface ConcessionarioService {
        // Promessa 1: "Ti darò una lista di concessionari filtrati"
    List<ConcessionarioDTO> searchConcessionari(String nome, String citta);

    // Promessa 2: "Salverò un concessionario partendo da un DTO"
    ConcessionarioDTO saveConcessionario(ConcessionarioDTO concessionarioDTO);

    void processExternalCheck(ConcessionarioDTO concessionarioDTO, String requestId); // Promessa 3: "Farò un controllo esterno (simulato) su un concessionario"
}
