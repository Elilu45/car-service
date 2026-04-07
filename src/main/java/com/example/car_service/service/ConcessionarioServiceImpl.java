package com.example.car_service.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.car_service.dto.ConcessionarioDTO;
import com.example.car_service.model.Concessionario;
import com.example.car_service.repository.ConcessionarioRepository;

import lombok.extern.slf4j.Slf4j;

@Service // <-- FONDAMENTALE: Dice a Spring che questa è la classe da "iniettare"
@Slf4j // <-- Questa annotazione crea automaticamente un oggetto chiamato 'log'
public class ConcessionarioServiceImpl implements ConcessionarioService {

    private final ConcessionarioRepository repository;

    // Dependency Injection tramite costruttore (quello che fanno i tuoi colleghi)
    public ConcessionarioServiceImpl(ConcessionarioRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public Concessionario saveConcessionario(ConcessionarioDTO concessionarioDTO) {
        log.info("Salvataggio concessionario: {} {}", concessionarioDTO.getNome(), concessionarioDTO.getCitta());

        Concessionario concessionario = new Concessionario();
        concessionario.setNome(concessionarioDTO.getNome());
        concessionario.setIndirizzo(concessionarioDTO.getIndirizzo());
        concessionario.setCitta(concessionarioDTO.getCitta());
        concessionario.setTelefono(concessionarioDTO.getTelefono());

        LocalDate oggi = LocalDate.now();
        concessionario.setDataApertura(oggi); // Impostiamo la data di registrazione a oggi

        String currentRequestId = MDC.get("x-Request-ID");
        concessionario.setRequestId(currentRequestId);
        
        log.info("Concessionario salvato: {} {}", concessionario.getNome(), concessionario.getCitta());
        return repository.save(concessionario);
    }

    @Async // <--- DICHIARA CHE IL METODO GIRA SU UN ALTRO THREAD
    @Override
    public void processExternalCheck(Concessionario concessionario, String requestId) {
        try {
            // Fondamentale: iniettiamo l'ID nel nuovo thread
            MDC.put("x-Request-ID", requestId);

            log.info("Inizio controllo esterno per: {} sito in {} aperto dal {}" , concessionario.getNome(), concessionario.getCitta(), LocalDate.now());

            // Simuliamo un'attesa di 5 secondi
            Thread.sleep(5000);

            // 1. Modifichi l'oggetto in memoria
            //car.setCheckAuto(true);
            
            // 2. FONDAMENTALE: Salvi di nuovo l'oggetto per aggiornare il DB
            //repository.save(car);
            
            log.info("Controllo completato per: {} sito in {} aperto dal {}", concessionario.getNome(), concessionario.getCitta(), concessionario.getDataApertura());

        } catch (InterruptedException e) {
            log.error("Errore durante il controllo asincrono", e);
        } finally {
            // Puliamo l'MDC anche qui per buona norma
            MDC.remove("x-Request-ID");
        }
    }



    @Override
    public List<Concessionario> searchConcessionari(String nome, String citta) {
        // Spostiamo qui la logica degli IF che avevi nel Controller
        if (nome != null && citta != null) {
            return repository.findByNomeIgnoreCaseContainingAndCittaIgnoreCaseContaining(nome, citta);
        } else if (nome != null) {
            return repository.findByNomeIgnoreCaseContaining(nome);
        } else if (citta != null) {
            return repository.findByCittaIgnoreCaseContaining(citta);
        } else {
            return repository.findAll();
        }
    }

}
