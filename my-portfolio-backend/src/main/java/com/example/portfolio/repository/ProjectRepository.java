package com.example.portfolio.repository;

import com.example.portfolio.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE p.category = ?1")
    List<Project> findByCategory(String category);
}


