package com.example.portfolio.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class KnowledgeGraphDTO {
    private List<KnowledgeNodeDTO> nodes;
    private List<KnowledgeRelationDTO> links;
    private Integer totalNodes;
    private Integer totalLinks;
}
