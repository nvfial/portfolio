package com.example.portfolio.service.impl;

import com.example.portfolio.model.dto.KnowledgeArticleDTO;
import com.example.portfolio.model.entity.ArticleStatus;
import com.example.portfolio.model.entity.KnowledgeArticle;
import com.example.portfolio.model.entity.KnowledgeCategory;
import com.example.portfolio.model.entity.User;
import com.example.portfolio.repository.KnowledgeArticleRepository;
import com.example.portfolio.repository.KnowledgeCategoryRepository;
import com.example.portfolio.repository.UserRepository;
import com.example.portfolio.service.KnowledgeArticleService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class KnowledgeArticleServiceImpl implements KnowledgeArticleService {

    @Autowired
    private KnowledgeArticleRepository articleRepository;

    @Autowired
    private KnowledgeCategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Page<KnowledgeArticleDTO> getArticlesByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<KnowledgeArticle> articles = articleRepository.findByCategory_IdAndIsPublishedTrue(categoryId, pageable);
        return articles.map(this::convertToDTO);
    }

    @Override
    public List<KnowledgeArticleDTO> getArticlesByCategory(Long categoryId) {
        List<KnowledgeArticle> articles = articleRepository.findByCategory_IdAndIsPublishedTrue(categoryId);
        return articles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeArticleDTO> getRecentArticles(int limit) {
        List<KnowledgeArticle> articles = articleRepository.findByIsPublishedTrueOrderByCreatedAtDesc();
        return articles.stream().limit(limit).map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public KnowledgeArticleDTO getArticleBySlug(String slug) {
        KnowledgeArticle article = articleRepository.findBySlug(slug)
            .orElseThrow(() -> new RuntimeException("Article not found: " + slug));
        
        article.setViewCount(article.getViewCount() + 1);
        articleRepository.save(article);
        
        return convertToDTO(article);
    }

    @Override
    public KnowledgeArticleDTO getArticleById(Long id) {
        KnowledgeArticle article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Article not found: " + id));
        return convertToDTO(article);
    }

    @Override
    public Optional<KnowledgeArticle> findById(Long id) {
        return articleRepository.findById(id);
    }

    @Override
    public KnowledgeArticleDTO createArticle(KnowledgeArticle article) {
        System.out.println("=== SERVICE createArticle ===");
        System.out.println("  article.getCategoryId(): " + article.getCategoryId());
        System.out.println("  article.getCategory(): " + article.getCategory());
        System.out.println("=============================");
        
        if (article.getAuthorId() != null) {
            User author = userRepository.findById(article.getAuthorId()).orElse(null);
            article.setAuthor(author);
        }
        
        System.out.println("  Checking categoryId: " + article.getCategoryId());
        System.out.println("  Current category: " + article.getCategory());
        
        if (article.getCategoryId() != null && article.getCategory() == null) {
            System.out.println("  --> Fetching category from database with id: " + article.getCategoryId());
            var categoryOpt = categoryRepository.findById(article.getCategoryId());
            if (categoryOpt.isPresent()) {
                KnowledgeCategory fetchedCategory = categoryOpt.get();
                System.out.println("  --> Category found in DB: id=" + fetchedCategory.getId() + ", name=" + fetchedCategory.getName());
                article.setCategory(fetchedCategory);
                System.out.println("  --> category set to: " + article.getCategory());
            } else {
                System.out.println("  --> WARNING: Category with id " + article.getCategoryId() + " NOT FOUND in database!");
            }
        } else if (article.getCategoryId() == null && article.getCategory() != null) {
            System.out.println("  --> categoryId is null but category exists, using existing category");
        } else if (article.getCategoryId() == null && article.getCategory() == null) {
            System.out.println("  --> BOTH categoryId AND category are null!");
        } else {
            System.out.println("  --> category already set, no fetch needed");
        }
        
        if (article.getStatus() == null) {
            article.setStatus(ArticleStatus.DRAFT);
        }
        
        if (article.getVersion() == null) {
            article.setVersion(1);
        }

        System.out.println("=== Before save ===");
        System.out.println("  article.getCategory(): " + article.getCategory());
        System.out.println("  article.getCategory() == null: " + (article.getCategory() == null));
        System.out.println("========================");

        KnowledgeArticle saved = articleRepository.save(article);
        return convertToDTO(saved);
    }

    @Override
    public KnowledgeArticleDTO updateArticle(Long id, KnowledgeArticle article) {
        KnowledgeArticle existing = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Article not found: " + id));

        existing.setTitle(article.getTitle());
        existing.setSlug(article.getSlug());
        existing.setContent(article.getContent());
        existing.setSummary(article.getSummary());
        existing.setCoverImage(article.getCoverImage());
        existing.setTags(article.getTags());
        existing.setReadTime(article.getReadTime());
        existing.setIsPublished(article.getIsPublished());
        existing.setIsFeatured(article.getIsFeatured());
        existing.setSeoTitle(article.getSeoTitle());
        existing.setSeoDescription(article.getSeoDescription());

        if (article.getAuthorId() != null && existing.getAuthor() == null) {
            User author = userRepository.findById(article.getAuthorId()).orElse(null);
            existing.setAuthor(author);
        }

        if (article.getStatus() != null) {
            existing.setStatus(article.getStatus());
        }

        KnowledgeArticle saved = articleRepository.save(existing);
        return convertToDTO(saved);
    }

    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        KnowledgeArticle article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Article not found: " + id));
        article.setViewCount(article.getViewCount() + 1);
        articleRepository.save(article);
    }

    @Override
    public void incrementLikeCount(Long id) {
        KnowledgeArticle article = articleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Article not found: " + id));
        article.setLikeCount(article.getLikeCount() + 1);
        articleRepository.save(article);
    }

    @Override
    public Page<KnowledgeArticleDTO> searchArticles(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.searchArticles(keyword, pageable).map(this::convertToDTO);
    }

    @Override
    public List<KnowledgeArticleDTO> getFeaturedArticles() {
        return articleRepository.findFeaturedArticles().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public Page<KnowledgeArticleDTO> getArticlesByStatus(ArticleStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findByStatus(status, pageable).map(this::convertToDTO);
    }

    @Override
    public Page<KnowledgeArticleDTO> getArticlesByFilters(ArticleStatus status, Long categoryId, Long authorId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return articleRepository.findByFilters(status, categoryId, authorId, keyword, pageable).map(this::convertToDTO);
    }

    @Override
    public long countByStatus(ArticleStatus status) {
        return articleRepository.countByStatus(status);
    }

    @Override
    public List<KnowledgeArticleDTO> getPendingReviewArticles() {
        return articleRepository.findByStatusOrderBySubmittedAtDesc(ArticleStatus.PENDING_REVIEW)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private KnowledgeArticleDTO convertToDTO(KnowledgeArticle article) {
        KnowledgeArticleDTO dto = new KnowledgeArticleDTO();
        dto.setId(article.getId());
        dto.setCategoryId(article.getCategory() != null ? article.getCategory().getId() : null);
        dto.setCategoryName(article.getCategory() != null ? article.getCategory().getName() : null);
        dto.setCategorySlug(article.getCategory() != null ? article.getCategory().getSlug() : null);
        dto.setDomainName(article.getCategory() != null && article.getCategory().getDomain() != null ? article.getCategory().getDomain().getName() : null);
        dto.setDomainSlug(article.getCategory() != null && article.getCategory().getDomain() != null ? article.getCategory().getDomain().getSlug() : null);
        dto.setTitle(article.getTitle());
        dto.setSlug(article.getSlug());
        dto.setContent(article.getContent());
        dto.setSummary(article.getSummary());
        dto.setCoverImage(article.getCoverImage());
        dto.setAuthor(article.getAuthor() != null ? article.getAuthor().getDisplayName() : article.getAuthorName());
        dto.setViewCount(article.getViewCount());
        dto.setLikeCount(article.getLikeCount());
        dto.setReadTime(article.getReadTime());
        dto.setIsPublished(article.getIsPublished());
        dto.setIsFeatured(article.getIsFeatured());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setUpdatedAt(article.getUpdatedAt());
        dto.setPublishedAt(article.getPublishedAt());
        dto.setStatus(article.getStatus() != null ? article.getStatus().name() : null);
        dto.setStatusDescription(article.getStatus() != null ? article.getStatus().getDescription() : null);

        if (article.getAuthor() != null) {
            dto.setAuthorId(article.getAuthor().getId());
        }

        if (article.getReviewer() != null) {
            dto.setReviewerId(article.getReviewer().getId());
            dto.setReviewerName(article.getReviewer().getDisplayName());
        }

        dto.setReviewComment(article.getReviewComment());
        dto.setSubmittedAt(article.getSubmittedAt());
        dto.setReviewedAt(article.getReviewedAt());
        dto.setVersion(article.getVersion());
        dto.setSeoTitle(article.getSeoTitle());
        dto.setSeoDescription(article.getSeoDescription());
        dto.setScore(article.getScore());

        if (article.getTags() != null) {
            try {
                List<String> tags = objectMapper.readValue(article.getTags(), new TypeReference<List<String>>() {});
                dto.setTags(tags);
            } catch (Exception e) {
                dto.setTags(new ArrayList<>());
            }
        }

        return dto;
    }
}