package com.example.portfolio.repository;

import com.example.portfolio.model.entity.KnowledgeDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KnowledgeDomainRepository extends JpaRepository<KnowledgeDomain, Long> {

    Optional<KnowledgeDomain> findBySlug(String slug);

    List<KnowledgeDomain> findByIsActiveTrueOrderBySortOrder();

    @Query("SELECT d FROM KnowledgeDomain d WHERE d.isActive = true ORDER BY d.sortOrder")
    List<KnowledgeDomain> findAllActive();
}
