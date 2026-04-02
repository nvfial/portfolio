package com.example.portfolio.service.impl;

import com.example.portfolio.model.dto.ProjectDTO;
import com.example.portfolio.model.entity.Project;
import com.example.portfolio.mapper.ProjectMapper;
import com.example.portfolio.repository.ProjectRepository;
import com.example.portfolio.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository repository;

    @Autowired
    private ProjectMapper mapper;

    @Override
    @Cacheable(value = "projects")
    public List<ProjectDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "projects", key = "#category")
    public List<ProjectDTO> getByCategory(String category) {
        return repository.findByCategory(category).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "projects", key = "featured")
    public List<ProjectDTO> getFeatured() {
        return repository.findByIsFeaturedTrueAndIsPublishedTrue().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Async
    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public CompletableFuture<ProjectDTO> save(ProjectDTO dto) {
        Project project = mapper.toEntity(dto);
        Project saved = repository.save(project);
        return CompletableFuture.completedFuture(mapper.toDTO(saved));
    }

    @Override
    @Cacheable(value = "projects", key = "#id")
    public ProjectDTO getById(Long id) {
        Project project = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        return mapper.toDTO(project);
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public ProjectDTO update(Long id, ProjectDTO dto) {
        Project project = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
        
        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setImageUrl(dto.getImageUrl());
        project.setCategory(dto.getCategory());
        project.setTags(dto.getTags());
        project.setLink(dto.getLink());
        project.setGithub(dto.getGithub());
        project.setGithubRepo(dto.getGithubRepo());
        project.setGithubRepoUrl(dto.getGithubRepoUrl());
        project.setStars(dto.getStars());
        project.setForks(dto.getForks());
        project.setWatchers(dto.getWatchers());
        project.setLanguage(dto.getLanguage());
        project.setPrimaryLanguage(dto.getPrimaryLanguage());
        project.setLanguages(dto.getLanguages());
        project.setLicense(dto.getLicense());
        project.setOpenIssues(dto.getOpenIssues());
        project.setLastCommit(dto.getLastCommit());
        project.setDescriptionExtended(dto.getDescriptionExtended());
        project.setTopics(dto.getTopics());
        project.setAnalysisStatus(dto.getAnalysisStatus());
        project.setAnalysisError(dto.getAnalysisError());
        project.setAnalyzedAt(dto.getAnalyzedAt());
        project.setFileCount(dto.getFileCount());
        project.setTotalLines(dto.getTotalLines());
        project.setReadmeContent(dto.getReadmeContent());
        project.setTechStack(dto.getTechStack());
        project.setSortOrder(dto.getSortOrder());
        project.setIsFeatured(dto.getIsFeatured());
        project.setIsPublished(dto.getIsPublished());
        
        Project updated = repository.save(project);
        return mapper.toDTO(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Project not found with id: " + id);
        }
        repository.deleteById(id);
    }
}