package com.example.portfolio.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "knowledge_nodes")
@Data
@EntityListeners(AuditingEntityListener.class)
public class KnowledgeNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private KnowledgeArticle article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_id")
    private KnowledgeDomain domain;

    @Column(nullable = false, length = 200)
    private String label;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 20)
    private String type = "keyword";

    @Column(name = "x_coord")
    private Float xCoord;

    @Column(name = "y_coord")
    private Float yCoord;

    @Column(name = "z_coord")
    private Float zCoord;

    @Column
    private Integer importance = 1;

    @Column(length = 20)
    private String color;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
