package com.example.portfolio.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectImportDTO {

    private String type;

    @NotBlank(message = "GitHub仓库地址或文件不能为空")
    private String source;

    private String category;
    private String link;
    private String github;
    private Integer sortOrder;
    private Boolean isFeatured;
    private Boolean isPublished;
}