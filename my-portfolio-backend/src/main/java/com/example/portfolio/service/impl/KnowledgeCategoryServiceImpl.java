package com.example.portfolio.service.impl;

import com.example.portfolio.model.dto.KnowledgeCategoryDTO;
import com.example.portfolio.model.entity.KnowledgeCategory;
import com.example.portfolio.model.entity.KnowledgeDomain;
import com.example.portfolio.repository.KnowledgeCategoryRepository;
import com.example.portfolio.repository.KnowledgeDomainRepository;
import com.example.portfolio.service.KnowledgeCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class KnowledgeCategoryServiceImpl implements KnowledgeCategoryService {

    @Autowired
    private KnowledgeCategoryRepository categoryRepository;

    @Autowired
    private KnowledgeDomainRepository domainRepository;

    @Override
    public List<KnowledgeCategoryDTO> getCategoriesByDomain(Long domainId) {
        List<KnowledgeCategory> categories = categoryRepository.findByDomainIdAndIsActiveTrueOrderBySortOrder(domainId);
        return categories.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeCategoryDTO> getAllCategories() {
        List<KnowledgeCategory> categories = categoryRepository.findAllActiveWithDomain();
        return categories.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public KnowledgeCategoryDTO getCategoryById(Long id) {
        KnowledgeCategory category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found: " + id));
        return convertToDTO(category);
    }

    @Override
    public KnowledgeCategoryDTO getCategoryBySlug(Long domainId, String slug) {
        KnowledgeCategory category = categoryRepository.findByDomainIdAndSlug(domainId, slug)
            .orElseThrow(() -> new RuntimeException("Category not found: " + slug));
        return convertToDTO(category);
    }

    @Override
    public KnowledgeCategoryDTO createCategory(KnowledgeCategory category) {
        KnowledgeDomain domain = domainRepository.findById(category.getDomain().getId())
            .orElseThrow(() -> new RuntimeException("Domain not found: " + category.getDomain().getId()));
        category.setDomain(domain);
        
        KnowledgeCategory saved = categoryRepository.save(category);
        return convertToDTO(saved);
    }

    @Override
    public KnowledgeCategoryDTO updateCategory(Long id, KnowledgeCategory category) {
        KnowledgeCategory existing = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found: " + id));

        existing.setName(category.getName());
        existing.setSlug(category.getSlug());
        existing.setDescription(category.getDescription());
        existing.setIcon(category.getIcon());
        existing.setColor(category.getColor());
        existing.setSortOrder(category.getSortOrder());
        existing.setIsActive(category.getIsActive());

        KnowledgeCategory saved = categoryRepository.save(existing);
        return convertToDTO(saved);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private KnowledgeCategoryDTO convertToDTO(KnowledgeCategory category) {
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
