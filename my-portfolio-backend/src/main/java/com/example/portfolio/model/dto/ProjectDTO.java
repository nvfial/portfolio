package com.example.portfolio.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectDTO {

    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "描述不能为空")
    private String description;

    private String imageUrl;
    private String category;
    private List<String> tags;
    private String link;
    private String github;
    private String githubRepo;
    private String githubRepoUrl;
    private Integer stars;
    private Integer forks;
    private Integer watchers;
    private String language;
    private String primaryLanguage;
    private String languages;
    private String license;
    private Integer openIssues;
    private LocalDateTime lastCommit;
    private String descriptionExtended;
    private String topics;
    private String analysisStatus;
    private String analysisError;
    private LocalDateTime analyzedAt;
    private Integer fileCount;
    private Integer totalLines;
    private String readmeContent;
    private String techStack;
    private List<ProjectImageDTO> images;
    private Integer sortOrder;
    private Boolean isFeatured;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class ProjectImageDTO {
        private Long id;
        private String url;
        private String altText;
        private String caption;
        private Integer sortOrder;
        private Boolean isCover;
    }
}