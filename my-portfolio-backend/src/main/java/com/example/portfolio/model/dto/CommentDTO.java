package com.example.portfolio.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDTO {
    private Long id;
    private Long parentId;
    private String userName;
    private String userEmail;
    private String userWebsite;
    private String userAvatar;
    private String content;
    private String targetType;
    private Long targetId;
    private String status;
    private Integer likesCount;
    private Boolean liked;
    private LocalDateTime createdAt;
    private List<CommentDTO> replies;
}
