package com.example.portfolio.service.impl;

import com.example.portfolio.model.dto.KnowledgeDomainDTO;
import com.example.portfolio.model.dto.KnowledgeCategoryDTO;
import com.example.portfolio.model.entity.KnowledgeDomain;
import com.example.portfolio.model.entity.KnowledgeCategory;
import com.example.portfolio.repository.KnowledgeDomainRepository;
import com.example.portfolio.repository.KnowledgeCategoryRepository;
import com.example.portfolio.repository.KnowledgeArticleRepository;
import com.example.portfolio.service.KnowledgeDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class KnowledgeDomainServiceImpl implements KnowledgeDomainService {

    @Autowired
    private KnowledgeDomainRepository domainRepository;

    @Autowired
    private KnowledgeCategoryRepository categoryRepository;

    @Autowired
    private KnowledgeArticleRepository articleRepository;

    @Override
    public List<KnowledgeDomainDTO> getAllDomains() {
        List<KnowledgeDomain> domains = domainRepository.findAllActive();
        return domains.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public KnowledgeDomainDTO getDomainBySlug(String slug) {
        KnowledgeDomain domain = domainRepository.findBySlug(slug)
            .orElseThrow(() -> new RuntimeException("Domain not found: " + slug));
        
        KnowledgeDomainDTO dto = convertToDTO(domain);
        
        List<KnowledgeCategory> categories = categoryRepository
            .findByDomainIdAndIsActiveTrueOrderBySortOrder(domain.getId());
        
        dto.setChildren(categories.stream()
            .map(this::convertCategoryToDTO)
            .collect(Collectors.toList()));
        
        return dto;
    }

    @Override
    public KnowledgeDomainDTO getDomainById(Long id) {
        KnowledgeDomain domain = domainRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Domain not found: " + id));
        return convertToDTO(domain);
    }

    @Override
    public KnowledgeDomainDTO createDomain(KnowledgeDomain domain) {
        KnowledgeDomain saved = domainRepository.save(domain);
        return convertToDTO(saved);
    }

    @Override
    public KnowledgeDomainDTO updateDomain(Long id, KnowledgeDomain domain) {
        KnowledgeDomain existing = domainRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Domain not found: " + id));
        
        existing.setName(domain.getName());
        existing.setSlug(domain.getSlug());
        existing.setDescription(domain.getDescription());
        existing.setIcon(domain.getIcon());
        existing.setColor(domain.getColor());
        existing.setSortOrder(domain.getSortOrder());
        existing.setIsActive(domain.getIsActive());
        
        KnowledgeDomain saved = domainRepository.save(existing);
        return convertToDTO(saved);
    }

    @Override
    public void deleteDomain(Long id) {
        domainRepository.deleteById(id);
    }

    @Override
    public List<KnowledgeDomainDTO> getActiveDomains() {
        return getAllDomains();
    }

    private KnowledgeDomainDTO convertToDTO(KnowledgeDomain domain) {
        KnowledgeDomainDTO dto = new KnowledgeDomainDTO();
        dto.setId(domain.getId());
        dto.setName(domain.getName());
        dto.setSlug(domain.getSlug());
        dto.setDescription(domain.getDescription());
        dto.setIcon(domain.getIcon());
        dto.setColor(domain.getColor());
        dto.setSortOrder(domain.getSortOrder());
        dto.setIsActive(domain.getIsActive());
        dto.setCreatedAt(domain.getCreatedAt());

        List<KnowledgeCategory> categories = categoryRepository
            .findByDomainIdAndIsActiveTrueOrderBySortOrder(domain.getId());
        
        dto.setCategoryCount(categories.size());
        
        long articleCount = categories.stream()
            .mapToLong(c -> categoryRepository.countPublishedArticlesByCategory(c.getId()))
            .sum();
        dto.setArticleCount(articleCount);

        dto.setChildren(categories.stream()
            .map(this::convertCategoryToDTO)
            .collect(Collectors.toList()));

        return dto;
    }

    private KnowledgeCategoryDTO convertCategoryToDTO(KnowledgeCategory category) {
        KnowledgeCategoryDTO dto = new KnowledgeCategoryDTO();
        dto.setId(category.getId());
        dto.setDomainId(category.getDomain().getId());
        dto.setDomainName(category.getDomain().getName());
        dto.setDomainSlug(category.getDomain().getSlug());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setIcon(category.getIcon());
        dto.setColor(category.getColor());
        dto.setSortOrder(category.getSortOrder());
        dto.setIsActive(category.getIsActive());
        dto.setCreatedAt(category.getCreatedAt());
        
        Long articleCount = categoryRepository.countPublishedArticlesByCategory(category.getId());
        dto.setArticleCount(articleCount);
        
        return dto;
    }
}
