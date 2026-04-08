package com.example.car_service.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.car_service.dto.ConcessionarioDTO;
import com.example.car_service.model.Concessionario;
import com.example.car_service.repository.ConcessionarioRepository;
import com.example.car_service.mapper.ConcessionarioMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service // <-- FONDAMENTALE: Dice a Spring che questa è la classe da "iniettare"
@Slf4j // <-- Questa annotazione crea automaticamente un oggetto chiamato 'log'
@RequiredArgsConstructor // <--- Genera il costruttore per tutti i campi "final"
public class ConcessionarioServiceImpl implements ConcessionarioService {

    private final ConcessionarioRepository repository;
    private final ConcessionarioMapper concessionarioMapper; // Aggiungi questo campo per usare MapStruct
    
    @Override
    public ConcessionarioDTO saveConcessionario(ConcessionarioDTO concessionarioDTO) {
        log.info("Salvataggio concessionario: {} {}", concessionarioDTO.getNome(), concessionarioDTO.getCitta());

        Concessionario concessionario = concessionarioMapper.toEntity(concessionarioDTO);

        LocalDate oggi = LocalDate.now();
        concessionario.setDataApertura(oggi); // Impostiamo la data di registrazione a oggi

        String currentRequestId = MDC.get("x-Request-ID");
        concessionario.setRequestId(currentRequestId);

        // Salva l'Entity
        Concessionario concessionarioSalvato = repository.save(concessionario);
        log.info("Concessionario salvato: {} {}", concessionarioSalvato.getNome(), concessionarioSalvato.getCitta());
        
        // 3. ENTITY -> DTO (Conversione per la Risposta)
        return concessionarioMapper.toDTO(repository.save(concessionario));
    }

    @Async // <--- DICHIARA CHE IL METODO GIRA SU UN ALTRO THREAD
    @Override
    public void processExternalCheck(ConcessionarioDTO concessionarioDto, String requestId) {
        try {
            // Fondamentale: iniettiamo l'ID nel nuovo thread
            MDC.put("x-Request-ID", requestId);

            log.info("Inizio controllo esterno per: {} sito in {} aperto dal {}" , concessionarioDto.getNome(), concessionarioDto.getCitta(), LocalDate.now());

            // Simuliamo un'attesa di 5 secondi
            Thread.sleep(5000);

            // 1. Modifichi l'oggetto in memoria
            //car.setCheckAuto(true);
            
            // 2. FONDAMENTALE: Salvi di nuovo l'oggetto per aggiornare il DB
            //repository.save(car);
            
            log.info("Controllo completato per: {} sito in {} aperto dal {}", concessionarioDto.getNome(), concessionarioDto.getCitta(), concessionarioDto.getDataApertura());

        } catch (InterruptedException e) {
            log.error("Errore durante il controllo asincrono", e);
        } finally {
            // Puliamo l'MDC anche qui per buona norma
            MDC.remove("x-Request-ID");
        }
    }



    @Override
    public List<ConcessionarioDTO> searchConcessionari(String nome, String citta) {
        List<Concessionario> entities;
        // 1. Cerchiamo le Entity (Mondo Interno)
        if (nome != null && citta != null) {
            entities = repository.findByNomeIgnoreCaseContainingAndCittaIgnoreCaseContaining(nome, citta);
        } else if (nome != null) {
            entities = repository.findByNomeIgnoreCaseContaining(nome);
        } else if (citta != null) {
            entities = repository.findByCittaIgnoreCaseContaining(citta);
        } else {
            entities = repository.findAll();
        }
                // 2. Trasformiamo la lista di Entity in una lista di DTO (Mondo Esterno)
        return concessionarioMapper.toDTOList(entities);
    }
}