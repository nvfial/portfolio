package com.example.portfolio.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestimonialDTO {

    private Long id;

    @NotBlank(message = "作者不能为空")
    private String author;

    @NotBlank(message = "内容不能为空")
    private String content;

    private LocalDateTime createdAt;
}






