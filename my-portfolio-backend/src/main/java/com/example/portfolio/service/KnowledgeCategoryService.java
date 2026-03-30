package com.example.portfolio.service;

import com.example.portfolio.model.dto.KnowledgeCategoryDTO;
import com.example.portfolio.model.entity.KnowledgeCategory;

import java.util.List;

public interface KnowledgeCategoryService {
    
    List<KnowledgeCategoryDTO> getCategoriesByDomain(Long domainId);
    
    List<KnowledgeCategoryDTO> getAllCategories();
    
    KnowledgeCategoryDTO getCategoryById(Long id);
    
    KnowledgeCategoryDTO getCategoryBySlug(Long domainId, String slug);
    
    KnowledgeCategoryDTO createCategory(KnowledgeCategory category);
    
    KnowledgeCategoryDTO updateCategory(Long id, KnowledgeCategory category);
    
    void deleteCategory(Long id);
}
