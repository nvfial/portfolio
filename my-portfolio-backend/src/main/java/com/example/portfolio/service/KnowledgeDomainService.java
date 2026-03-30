package com.example.portfolio.service;

import com.example.portfolio.model.dto.KnowledgeDomainDTO;
import com.example.portfolio.model.entity.KnowledgeDomain;

import java.util.List;

public interface KnowledgeDomainService {
    
    List<KnowledgeDomainDTO> getAllDomains();
    
    KnowledgeDomainDTO getDomainBySlug(String slug);
    
    KnowledgeDomainDTO getDomainById(Long id);
    
    KnowledgeDomainDTO createDomain(KnowledgeDomain domain);
    
    KnowledgeDomainDTO updateDomain(Long id, KnowledgeDomain domain);
    
    void deleteDomain(Long id);
    
    List<KnowledgeDomainDTO> getActiveDomains();
}
