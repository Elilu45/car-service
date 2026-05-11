package com.example.car_service.service;

import com.example.car_service.dto.CarEventLogDTO;
import java.util.List;


public interface CarEventLogService {
    List<CarEventLogDTO> getAllLogs();
}
