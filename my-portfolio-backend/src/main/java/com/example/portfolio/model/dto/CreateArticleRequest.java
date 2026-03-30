package com.example.portfolio.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateArticleRequest {
    private String title;
    private String slug;
    private String content;
    private String summary;
    private String coverImage;
    private String authorName;
    private String tags;
    private Integer readTime;
    
    @JsonProperty("categoryId")
    private Long categoryId;
    
    @JsonProperty("authorId")
    private Long authorId;
    
    private Boolean isPublished;
    private Boolean isFeatured;
    private String status;
}