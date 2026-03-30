package com.example.portfolio.model.dto;

import lombok.Data;

@Data
public class KnowledgeArticleDTO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private String domainName;
    private String domainSlug;
    private String title;
    private String slug;
    private String content;
    private String summary;
    private String coverImage;
    private String author;
    private Long authorId;
    private Integer viewCount;
    private Integer likeCount;
    private Integer readTime;
    private Boolean isPublished;
    private Boolean isFeatured;
    private String status;
    private String statusDescription;
    private Long reviewerId;
    private String reviewerName;
    private String reviewComment;
    private java.time.LocalDateTime submittedAt;
    private java.time.LocalDateTime reviewedAt;
    private java.time.LocalDateTime publishedAt;
    private Integer version;
    private String seoTitle;
    private String seoDescription;
    private java.math.BigDecimal score;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
    private java.util.List<String> tags;
}