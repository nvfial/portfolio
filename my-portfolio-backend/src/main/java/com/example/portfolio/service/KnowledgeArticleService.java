package com.example.portfolio.service;

import com.example.portfolio.model.dto.KnowledgeArticleDTO;
import com.example.portfolio.model.entity.ArticleStatus;
import com.example.portfolio.model.entity.KnowledgeArticle;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface KnowledgeArticleService {
    
    Page<KnowledgeArticleDTO> getArticlesByCategory(Long categoryId, int page, int size);
    
    List<KnowledgeArticleDTO> getArticlesByCategory(Long categoryId);
    
    List<KnowledgeArticleDTO> getRecentArticles(int limit);
    
    KnowledgeArticleDTO getArticleBySlug(String slug);
    
    KnowledgeArticleDTO getArticleById(Long id);
    
    Optional<KnowledgeArticle> findById(Long id);
    
    KnowledgeArticleDTO createArticle(KnowledgeArticle article);
    
    KnowledgeArticleDTO updateArticle(Long id, KnowledgeArticle article);
    
    void deleteArticle(Long id);
    
    void incrementViewCount(Long id);
    
    void incrementLikeCount(Long id);
    
    Page<KnowledgeArticleDTO> searchArticles(String keyword, int page, int size);
    
    List<KnowledgeArticleDTO> getFeaturedArticles();
    
    Page<KnowledgeArticleDTO> getArticlesByStatus(ArticleStatus status, int page, int size);
    
    Page<KnowledgeArticleDTO> getArticlesByFilters(ArticleStatus status, Long categoryId, Long authorId, String keyword, int page, int size);
    
    long countByStatus(ArticleStatus status);
    
    List<KnowledgeArticleDTO> getPendingReviewArticles();
}