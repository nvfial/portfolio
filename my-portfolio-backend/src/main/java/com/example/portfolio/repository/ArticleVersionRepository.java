package com.example.portfolio.repository;

import com.example.portfolio.model.entity.ArticleVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleVersionRepository extends JpaRepository<ArticleVersion, Long> {
    
    List<ArticleVersion> findByArticleIdOrderByVersionDesc(Long articleId);
    
    Optional<ArticleVersion> findByArticleIdAndVersion(Long articleId, Integer version);
    
    @Query("SELECT MAX(v.version) FROM ArticleVersion v WHERE v.article.id = :articleId")
    Integer findMaxVersionByArticleId(Long articleId);
}