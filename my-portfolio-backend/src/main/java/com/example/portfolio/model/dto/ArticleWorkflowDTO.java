package com.example.portfolio.model.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleWorkflowDTO {
    private Long articleId;
    private String currentStatus;
    private String statusDescription;
    private List<StatusTransition> availableTransitions;
    private Long authorId;
    private String authorName;
    private Long reviewerId;
    private String reviewerName;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private String reviewComment;
    private Integer version;
    
    @Data
    public static class StatusTransition {
        private String targetStatus;
        private String targetDescription;
        private String action;
        private String requiredRole;
        private boolean allowed;
    }
}