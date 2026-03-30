package com.example.portfolio.service;

import com.example.portfolio.model.dto.KnowledgeGraphDTO;
import com.example.portfolio.model.dto.KnowledgeNodeDTO;
import com.example.portfolio.model.dto.KnowledgeRelationDTO;
import com.example.portfolio.model.entity.KnowledgeNode;
import com.example.portfolio.model.entity.KnowledgeRelation;

import java.util.List;

public interface KnowledgeGraphService {
    
    KnowledgeGraphDTO getGraphByDomain(Long domainId);
    
    KnowledgeGraphDTO getFullGraph();
    
    List<KnowledgeNodeDTO> getNodesByDomain(Long domainId);
    
    List<KnowledgeNodeDTO> getAllNodes();
    
    KnowledgeNodeDTO createNode(KnowledgeNode node);
    
    KnowledgeNodeDTO updateNode(Long id, KnowledgeNode node);
    
    void deleteNode(Long id);
    
    List<KnowledgeRelationDTO> getRelationsByNodeId(Long nodeId);
    
    KnowledgeRelationDTO createRelation(KnowledgeRelation relation);
    
    void deleteRelation(Long id);
    
    void deleteRelationsByNodeId(Long nodeId);
}
