package com.example.portfolio.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgeCategoryDTO {
    private Long id;
    private Long domainId;
    private String domainName;
    private String domainSlug;
    private String name;
    private String slug;
    private String description;
    private String icon;
    private String color;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Long articleCount;
}
