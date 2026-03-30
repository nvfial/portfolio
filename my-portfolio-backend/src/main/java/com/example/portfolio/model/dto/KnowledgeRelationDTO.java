package com.example.portfolio.model.dto;

import lombok.Data;

@Data
public class KnowledgeRelationDTO {
    private Long id;
    private Long sourceNodeId;
    private Long targetNodeId;
    private String relationType;
    private Integer weight;
}
