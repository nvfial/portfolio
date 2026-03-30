package com.example.portfolio.repository;

import com.example.portfolio.model.entity.ArticleStatus;
import com.example.portfolio.model.entity.KnowledgeArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticle, Long> {

    Page<KnowledgeArticle> findByCategory_IdAndIsPublishedTrue(Long categoryId, Pageable pageable);

    List<KnowledgeArticle> findByCategory_IdAndIsPublishedTrue(Long categoryId);

    List<KnowledgeArticle> findByIsPublishedTrueOrderByCreatedAtDesc();

    Optional<KnowledgeArticle> findBySlug(String slug);

    Optional<KnowledgeArticle> findById(Long id);

    @Query("SELECT a FROM KnowledgeArticle a WHERE a.isPublished = true ORDER BY a.viewCount DESC")
    List<KnowledgeArticle> findTopViewedArticles(Pageable pageable);

    @Query("SELECT a FROM KnowledgeArticle a WHERE a.isPublished = true AND a.isFeatured = true")
    List<KnowledgeArticle> findFeaturedArticles();

    @Query("SELECT a FROM KnowledgeArticle a WHERE a.isPublished = true AND (a.title LIKE %:keyword% OR a.content LIKE %:keyword%)")
    Page<KnowledgeArticle> searchArticles(String keyword, Pageable pageable);

    Page<KnowledgeArticle> findByStatus(ArticleStatus status, Pageable pageable);

    Page<KnowledgeArticle> findByCategoryIdAndStatus(Long categoryId, ArticleStatus status, Pageable pageable);

    Page<KnowledgeArticle> findByAuthorId(Long authorId, Pageable pageable);

    Page<KnowledgeArticle> findByStatusOrAuthorId(ArticleStatus status, Long authorId, Pageable pageable);

    @Query("SELECT a FROM KnowledgeArticle a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:categoryId IS NULL OR a.category.id = :categoryId) AND " +
           "(:authorId IS NULL OR a.author.id = :authorId) AND " +
           "(:keyword IS NULL OR a.title LIKE %:keyword%)")
    Page<KnowledgeArticle> findByFilters(
            @Param("status") ArticleStatus status,
            @Param("categoryId") Long categoryId,
            @Param("authorId") Long authorId,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT COUNT(a) FROM KnowledgeArticle a WHERE a.status = :status")
    long countByStatus(@Param("status") ArticleStatus status);

    List<KnowledgeArticle> findByStatusOrderBySubmittedAtDesc(ArticleStatus status);

    Page<KnowledgeArticle> findByIsPublishedTrue(Pageable pageable);

    Page<KnowledgeArticle> findByIsPublishedFalse(Pageable pageable);
}