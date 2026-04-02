package com.example.portfolio.repository;

import com.example.portfolio.model.entity.ResourceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResourceCategoryRepository extends JpaRepository<ResourceCategory, Long> {
    List<ResourceCategory> findByIsActiveTrueOrderBySortOrderAsc();
    List<ResourceCategory> findByParentIdOrderBySortOrderAsc(Long parentId);
}