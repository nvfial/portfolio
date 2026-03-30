package com.example.portfolio.service.impl;

import com.example.portfolio.model.dto.KnowledgeGraphDTO;
import com.example.portfolio.model.dto.KnowledgeNodeDTO;
import com.example.portfolio.model.dto.KnowledgeRelationDTO;
import com.example.portfolio.model.entity.KnowledgeNode;
import com.example.portfolio.model.entity.KnowledgeRelation;
import com.example.portfolio.repository.KnowledgeNodeRepository;
import com.example.portfolio.repository.KnowledgeRelationRepository;
import com.example.portfolio.service.KnowledgeGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class KnowledgeGraphServiceImpl implements KnowledgeGraphService {

    @Autowired
    private KnowledgeNodeRepository nodeRepository;

    @Autowired
    private KnowledgeRelationRepository relationRepository;

    @Override
    public KnowledgeGraphDTO getGraphByDomain(Long domainId) {
        List<KnowledgeNode> nodes = nodeRepository.findByDomainId(domainId);
        List<KnowledgeRelation> relations = relationRepository.findByDomainId(domainId);

        KnowledgeGraphDTO dto = new KnowledgeGraphDTO();
        dto.setNodes(nodes.stream().map(this::convertNodeToDTO).collect(Collectors.toList()));
        dto.setLinks(relations.stream().map(this::convertRelationToDTO).collect(Collectors.toList()));
        dto.setTotalNodes(nodes.size());
        dto.setTotalLinks(relations.size());

        return dto;
    }

    @Override
    public KnowledgeGraphDTO getFullGraph() {
        List<KnowledgeNode> nodes = nodeRepository.findAll();
        List<KnowledgeRelation> relations = relationRepository.findAll();

        KnowledgeGraphDTO dto = new KnowledgeGraphDTO();
        dto.setNodes(nodes.stream().map(this::convertNodeToDTO).collect(Collectors.toList()));
        dto.setLinks(relations.stream().map(this::convertRelationToDTO).collect(Collectors.toList()));
        dto.setTotalNodes(nodes.size());
        dto.setTotalLinks(relations.size());

        return dto;
    }

    @Override
    public List<KnowledgeNodeDTO> getNodesByDomain(Long domainId) {
        return nodeRepository.findByDomainId(domainId)
            .stream()
            .map(this::convertNodeToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeNodeDTO> getAllNodes() {
        return nodeRepository.findAll()
            .stream()
            .map(this::convertNodeToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public KnowledgeNodeDTO createNode(KnowledgeNode node) {
        KnowledgeNode saved = nodeRepository.save(node);
        return convertNodeToDTO(saved);
    }

    @Override
    public KnowledgeNodeDTO updateNode(Long id, KnowledgeNode node) {
        KnowledgeNode existing = nodeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Node not found: " + id));

        existing.setLabel(node.getLabel());
        existing.setDescription(node.getDescription());
        existing.setType(node.getType());
        existing.setXCoord(node.getXCoord());
        existing.setYCoord(node.getYCoord());
        existing.setZCoord(node.getZCoord());
        existing.setImportance(node.getImportance());
        existing.setColor(node.getColor());

        KnowledgeNode saved = nodeRepository.save(existing);
        return convertNodeToDTO(saved);
    }

    @Override
    public void deleteNode(Long id) {
        KnowledgeNode node = nodeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Node not found: " + id));
        
        relationRepository.deleteBySourceNodeOrTargetNode(node, node);
        nodeRepository.deleteById(id);
    }

    @Override
    public List<KnowledgeRelationDTO> getRelationsByNodeId(Long nodeId) {
        return relationRepository.findByNodeId(nodeId)
            .stream()
            .map(this::convertRelationToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public KnowledgeRelationDTO createRelation(KnowledgeRelation relation) {
        KnowledgeRelation saved = relationRepository.save(relation);
        return convertRelationToDTO(saved);
    }

    @Override
    public void deleteRelation(Long id) {
        relationRepository.deleteById(id);
    }

    @Override
    public void deleteRelationsByNodeId(Long nodeId) {
        KnowledgeNode node = nodeRepository.findById(nodeId)
            .orElseThrow(() -> new RuntimeException("Node not found: " + nodeId));
        relationRepository.deleteBySourceNodeOrTargetNode(node, node);
    }

    private KnowledgeNodeDTO convertNodeToDTO(KnowledgeNode node) {
        KnowledgeNodeDTO dto = new KnowledgeNodeDTO();
        dto.setId(node.getId());
        
        if (node.getArticle() != null) {
            dto.setArticleId(node.getArticle().getId());
        }
        if (node.getDomain() != null) {
            dto.setDomainId(node.getDomain().getId());
        }
        
        dto.setLabel(node.getLabel());
        dto.setDescription(node.getDescription());
        dto.setType(node.getType());
        dto.setXCoord(node.getXCoord());
        dto.setYCoord(node.getYCoord());
        dto.setZCoord(node.getZCoord());
        dto.setImportance(node.getImportance());
        dto.setColor(node.getColor());
        dto.setCreatedAt(node.getCreatedAt());

        return dto;
    }

    private KnowledgeRelationDTO convertRelationToDTO(KnowledgeRelation relation) {
        KnowledgeRelationDTO dto = new KnowledgeRelationDTO();
        dto.setId(relation.getId());
        dto.setSourceNodeId(relation.getSourceNode().getId());
        dto.setTargetNodeId(relation.getTargetNode().getId());
        dto.setRelationType(relation.getRelationType());
        dto.setWeight(relation.getWeight());

        return dto;
    }
}
