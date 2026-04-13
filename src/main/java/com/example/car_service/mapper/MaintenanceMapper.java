package com.example.car_service.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.car_service.dto.MaintenanceDTO;
import com.example.car_service.model.Maintenance;

@Mapper(componentModel = "spring")
public interface MaintenanceMapper {
    // 1. DA ENTITY A DTO (Output verso Postman)
    // Diciamo a MapStruct: prendi l'ID che sta DENTRO l'oggetto car
    // e mettilo nel campo 'carId' del DTO.
    @Mapping(target = "carId", source = "car.id")
    MaintenanceDTO toDTO(Maintenance maintenance);
    // 2. DA DTO A ENTITY (Input da Postman)
    // Qui ignoriamo la car perché nel Service dovrai comunque fare 
    // la findById sul database per recuperare l'oggetto reale.
    // Ignoriamo anche l'ID perché lo genera il database.
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestId", ignore = true)
    Maintenance toEntity(MaintenanceDTO maintenanceDTO);
    // 3. MAPPARE LE LISTE
    // Questo trasformerà una List<Maintenance> in List<MaintenanceDTO> usando la logica definita sopra
    List<MaintenanceDTO> toDTOList(List<Maintenance> maintenances);
}