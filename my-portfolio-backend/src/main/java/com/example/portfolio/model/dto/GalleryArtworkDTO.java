package com.example.portfolio.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GalleryArtworkDTO {
    private Long id;
    private Long collectionId;
    private String collectionName;
    private String title;
    private String description;
    private String imageUrl;
    private String thumbnailUrl;
    private String artist;
    private String year;
    private String medium;
    private String dimensions;
    private List<String> tags;
    private Integer viewCount;
    private Integer likeCount;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
