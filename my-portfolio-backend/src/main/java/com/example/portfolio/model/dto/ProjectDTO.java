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
    private LocalDateTime createdAt;
}






