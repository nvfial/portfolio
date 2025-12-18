package com.example.portfolio.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VisitLogDTO {
    private Long id;
    private String ip;
    private String userAgent;
    private String referrer;
    private String language;
    private String screenResolution;
    private String path;
    private LocalDateTime createdAt;
}






