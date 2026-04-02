package com.example.portfolio.repository;

import com.example.portfolio.model.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    
    Page<Resource> findByIsActiveTrue(Pageable pageable);
    
    Page<Resource> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);
    
    Page<Resource> findByTypeAndIsActiveTrue(String type, Pageable pageable);
    
    @Query("SELECT r FROM Resource r WHERE r.isActive = true AND " +
           "(r.name LIKE %:keyword% OR r.description LIKE %:keyword%)")
    Page<Resource> search(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT r FROM Resource r WHERE r.isActive = true ORDER BY r.downloadCount DESC")
    Page<Resource> findPopular(Pageable pageable);
    
    @Query("SELECT r FROM Resource r WHERE r.isActive = true ORDER BY r.createdAt DESC")
    Page<Resource> findRecent(Pageable pageable);
    
    List<Resource> findByCategoryIdAndIsActiveTrueOrderBySortOrderAsc(Long categoryId);
    
    @Query("SELECT COUNT(r) FROM Resource r WHERE r.categoryId = :categoryId AND r.isActive = true")
    Long countByCategoryId(@Param("categoryId") Long categoryId);
}