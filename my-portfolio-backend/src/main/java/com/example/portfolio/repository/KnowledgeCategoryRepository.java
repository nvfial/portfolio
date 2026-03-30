package com.example.portfolio.repository;

import com.example.portfolio.model.entity.KnowledgeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KnowledgeCategoryRepository extends JpaRepository<KnowledgeCategory, Long> {

    List<KnowledgeCategory> findByDomainIdAndIsActiveTrueOrderBySortOrder(Long domainId);

    List<KnowledgeCategory> findByIsActiveTrueOrderBySortOrder();

    Optional<KnowledgeCategory> findByDomainIdAndSlug(Long domainId, String slug);

    @Query("SELECT c FROM KnowledgeCategory c LEFT JOIN FETCH c.domain WHERE c.isActive = true ORDER BY c.sortOrder")
    List<KnowledgeCategory> findAllActiveWithDomain();

    @Query("SELECT COUNT(a) FROM KnowledgeArticle a WHERE a.category.id = :categoryId AND a.isPublished = true")
    Long countPublishedArticlesByCategory(Long categoryId);
}
