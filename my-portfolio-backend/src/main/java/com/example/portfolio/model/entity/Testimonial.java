package com.example.portfolio.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "testimonials", indexes = {
    @Index(name = "idx_is_featured", columnList = "is_featured"),
    @Index(name = "idx_sort_order", columnList = "sort_order"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@EntityListeners(AuditingEntityListener.class)
public class Testimonial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(length = 100)
    private String company;

    @Column(length = 100)
    private String position;

    @Column(name = "is_featured")
    private Boolean isFeatured;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_published")
    private Boolean isPublished;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}