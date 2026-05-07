package com.example.car_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.car_service.dto.CarEventLogDTO;
import com.example.car_service.mapper.CarEventLogMapper;
import com.example.car_service.model.CarEventLog;
import com.example.car_service.repository.CarEventLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarEventLogServiceImpl implements CarEventLogService {

    private final CarEventLogRepository logRepository;
    private final CarEventLogMapper carEventLogMapper;

    @Override
    public List<CarEventLogDTO> getAllLogs() {
        List<CarEventLog> entities = logRepository.findAll();

        return carEventLogMapper.toDTOList(entities);
    }
}