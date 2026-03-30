package com.example.portfolio.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class KnowledgeDomainDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String icon;
    private String color;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<KnowledgeCategoryDTO> children;
    private Integer categoryCount;
    private Long articleCount;
}
