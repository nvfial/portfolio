package com.example.portfolio.model.dto;

import lombok.Data;

@Data
public class GalleryCollectionDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String coverImage;
    private String category;
    private Integer sortOrder;
    private Boolean isActive;
    private Integer artworkCount;
}
