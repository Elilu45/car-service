package com.example.car_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.example.car_service.dto.ConcessionarioDTO;
import com.example.car_service.model.Concessionario;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ConcessionarioMapper {

    // 1. DA ENTITY A DTO (Output verso Postman)
    // Diciamo a MapStruct: prendi l'ID che sta DENTRO l'oggetto concessionario
    // e mettilo nel campo 'concessionarioId' del DTO.
    ConcessionarioDTO toDTO(Concessionario concessionario);

    // 2. DA DTO A ENTITY (Input da Postman)
    // Qui ignoriamo il concessionario perché nel Service dovrai comunque fare 
    // la findById sul database per recuperare l'oggetto reale.
    // Ignoriamo anche l'ID perché lo genera il database.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestId", ignore = true) // <--- AGGIUNGI QUESTA RIGA
    @Mapping(target = "listaAuto", ignore = true) // Ignoriamo la lista di auto per evitare problemi di mapping circolare
    Concessionario toEntity(ConcessionarioDTO concessionarioDTO);

    // 3. MAPPARE LE LISTE
    // Questo trasformerà una List<Concessionario> in List<ConcessionarioDTO> usando la logica definita sopra
    List<ConcessionarioDTO> toDTOList(List<Concessionario> concessionari);
}
