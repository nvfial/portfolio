package com.example.portfolio.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgeNodeDTO {
    private Long id;
    private Long articleId;
    private Long domainId;
    private String label;
    private String description;
    private String type;
    private Float xCoord;
    private Float yCoord;
    private Float zCoord;
    private Integer importance;
    private String color;
    private LocalDateTime createdAt;
}
