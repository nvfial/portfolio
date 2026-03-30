package com.example.portfolio.repository;

import com.example.portfolio.model.entity.KnowledgeNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeNodeRepository extends JpaRepository<KnowledgeNode, Long> {

    List<KnowledgeNode> findByDomainId(Long domainId);

    List<KnowledgeNode> findByArticleId(Long articleId);

    List<KnowledgeNode> findByType(String type);

    @Query("SELECT n FROM KnowledgeNode n WHERE n.domain IS NOT NULL")
    List<KnowledgeNode> findAllDomainNodes();

    @Query("SELECT n FROM KnowledgeNode n WHERE n.article IS NOT NULL")
    List<KnowledgeNode> findAllArticleNodes();
}
