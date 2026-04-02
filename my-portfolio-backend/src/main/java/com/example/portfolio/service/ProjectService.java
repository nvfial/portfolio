package com.example.portfolio.service;

import com.example.portfolio.model.dto.ProjectDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProjectService {

    List<ProjectDTO> getAll();

    List<ProjectDTO> getByCategory(String category);

    List<ProjectDTO> getFeatured();

    CompletableFuture<ProjectDTO> save(ProjectDTO dto);

    ProjectDTO getById(Long id);

    ProjectDTO update(Long id, ProjectDTO dto);

    void delete(Long id);
}