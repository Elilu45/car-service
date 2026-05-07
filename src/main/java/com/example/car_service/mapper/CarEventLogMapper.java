package com.example.car_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.car_service.dto.CarEventLogDTO;
import com.example.car_service.model.CarEventLog;

@Mapper(componentModel = "spring")
public interface CarEventLogMapper {
        // 1. DA ENTITY A DTO (Output verso Postman)
    CarEventLogDTO toDTO(CarEventLog eventLog);

    // 2. DA DTO A ENTITY (Input da Postman)
    // Qui ignoriamo il concessionario perché nel Service dovrai comunque fare 
    // la findById sul database per recuperare l'oggetto reale.
    // Ignoriamo anche l'ID perché lo genera il database.
    @Mapping(target = "id", ignore = true)
    CarEventLog toEntity(CarEventLogDTO eventLogDTO);

    // 3. MAPPARE LE LISTE
    // Questo trasformerà una List<CarEventLog> in List<CarEventLogDTO> usando la logica definita sopra
    List<CarEventLogDTO> toDTOList(List<CarEventLog> eventLogs);
}
