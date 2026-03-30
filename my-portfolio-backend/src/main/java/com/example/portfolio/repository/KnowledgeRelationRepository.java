package com.example.portfolio.repository;

import com.example.portfolio.model.entity.KnowledgeRelation;
import com.example.portfolio.model.entity.KnowledgeNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeRelationRepository extends JpaRepository<KnowledgeRelation, Long> {

    List<KnowledgeRelation> findBySourceNodeId(Long sourceNodeId);

    List<KnowledgeRelation> findByTargetNodeId(Long targetNodeId);

    @Query("SELECT r FROM KnowledgeRelation r WHERE r.sourceNode.id = :nodeId OR r.targetNode.id = :nodeId")
    List<KnowledgeRelation> findByNodeId(Long nodeId);

    @Query("SELECT r FROM KnowledgeRelation r WHERE r.sourceNode.domain.id = :domainId OR r.targetNode.domain.id = :domainId")
    List<KnowledgeRelation> findByDomainId(Long domainId);

    void deleteBySourceNodeOrTargetNode(KnowledgeNode source, KnowledgeNode target);
}
