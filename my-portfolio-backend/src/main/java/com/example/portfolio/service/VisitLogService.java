package com.example.portfolio.service;

import com.example.portfolio.model.dto.VisitLogDTO;

import java.util.Map;

import java.util.Map;

public interface VisitLogService {
    VisitLogDTO save(VisitLogDTO dto);
    Map<String, Object> getVisitStats();
    Map<String, Object> getIpStats();
    Map<String, Object> getPathStats();
    Map<String, Object> getDashboardData();
}

