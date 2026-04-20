package com.example.car_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.example.car_service.dto.CarDTO;
import com.example.car_service.model.Car;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {

    // 1. DA ENTITY A DTO (Output verso Postman)
    // Diciamo a MapStruct: prendi l'ID che sta DENTRO l'oggetto concessionario
    // e mettilo nel campo 'concessionarioId' del DTO.
    @Mapping(target = "concessionarioId", source = "concessionario.id")
    CarDTO toDTO(Car car);

    // 2. DA DTO A ENTITY (Input da Postman)
    // Qui ignoriamo il concessionario perché nel Service dovrai comunque fare 
    // la findById sul database per recuperare l'oggetto reale.
    // Ignoriamo anche l'ID perché lo genera il database.
    @Mapping(target = "concessionario", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requestId", ignore = true) // <--- AGGIUNGI QUESTA RIGA
    // Se checkAuto nel DTO è null, MapStruct imposterà false nell'Entity
    @Mapping(target = "maintenances", ignore = true) // <--- AGGIUNGI QUESTA PER TOGLIERE IL WARNING
    @Mapping(target = "checkAuto", source = "checkAuto", defaultValue = "false")
    Car toEntity(CarDTO carDTO);

    // 3. MAPPARE LE LISTE
    // Questo trasformerà una List<Car> in List<CarDTO> usando la logica definita sopra
    List<CarDTO> toDTOList(List<Car> cars);
}