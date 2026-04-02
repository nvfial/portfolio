package com.example.portfolio.repository;

import com.example.portfolio.model.entity.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectImageRepository extends JpaRepository<ProjectImage, Long> {
    List<ProjectImage> findByProjectIdOrderBySortOrderAsc(Long projectId);
    void deleteByProjectId(Long projectId);
}