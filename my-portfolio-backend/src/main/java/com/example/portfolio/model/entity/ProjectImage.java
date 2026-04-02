package com.example.portfolio.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_images", indexes = {
    @Index(name = "idx_project_id", columnList = "project_id"),
    @Index(name = "idx_sort_order", columnList = "sort_order")
})
@Data
@EntityListeners(AuditingEntityListener.class)
public class ProjectImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String url;

    @Column(name = "alt_text")
    private String altText;

    @Column(name = "caption")
    private String caption;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_cover")
    private Boolean isCover;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}