package com.example.car_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.car_service.model.Concessionario;

public interface ConcessionarioRepository extends JpaRepository<Concessionario, Long> {
    
    List<Concessionario> findByNomeIgnoreCaseContainingAndCittaIgnoreCaseContaining(String nome, String citta);

    List<Concessionario> findByNomeIgnoreCaseContaining(String nome);

    List<Concessionario> findByCittaIgnoreCaseContaining(String citta);
}
