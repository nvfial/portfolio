package com.example.portfolio.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects", indexes = {
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_analysis_status", columnList = "analysis_status"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@EntityListeners(AuditingEntityListener.class)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    private String category;

    @ElementCollection
    @CollectionTable(name = "project_tags", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    private String link;

    private String github;

    @Column(name = "github_repo", length = 255)
    private String githubRepo;

    @Column(name = "github_repo_url")
    private String githubRepoUrl;

    @Column(name = "stars")
    private Integer stars;

    @Column(name = "forks")
    private Integer forks;

    @Column(name = "watchers")
    private Integer watchers;

    @Column(name = "language", length = 100)
    private String language;

    @Column(name = "primary_language", length = 100)
    private String primaryLanguage;

    @Column(name = "languages", columnDefinition = "JSON")
    private String languages;

    @Column(name = "license")
    private String license;

    @Column(name = "open_issues")
    private Integer openIssues;

    @Column(name = "last_commit")
    private LocalDateTime lastCommit;

    @Column(name = "description_extended", columnDefinition = "TEXT")
    private String descriptionExtended;

    @Column(name = "topics", columnDefinition = "JSON")
    private String topics;

    @Column(name = "analysis_status", length = 20)
    private String analysisStatus;

    @Column(name = "analysis_error", columnDefinition = "TEXT")
    private String analysisError;

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    @Column(name = "file_count")
    private Integer fileCount;

    @Column(name = "total_lines")
    private Integer totalLines;

    @Column(name = "readme_content", columnDefinition = "TEXT")
    private String readmeContent;

    @Column(name = "tech_stack", columnDefinition = "JSON")
    private String techStack;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectImage> images = new ArrayList<>();

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_featured")
    private Boolean isFeatured;

    @Column(name = "is_published")
    private Boolean isPublished;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}