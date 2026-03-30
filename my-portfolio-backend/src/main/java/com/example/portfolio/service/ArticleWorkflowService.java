package com.example.portfolio.service;

import com.example.portfolio.model.dto.ArticleWorkflowDTO;
import com.example.portfolio.model.dto.WorkflowRequest;
import com.example.portfolio.model.entity.ArticleStatus;
import com.example.portfolio.model.entity.KnowledgeArticle;

public interface ArticleWorkflowService {
    
    ArticleWorkflowDTO getWorkflowState(Long articleId);
    
    KnowledgeArticle submitForReview(Long articleId, Long authorId, WorkflowRequest request);
    
    KnowledgeArticle approveArticle(Long articleId, Long reviewerId, WorkflowRequest request);
    
    KnowledgeArticle rejectArticle(Long articleId, Long reviewerId, WorkflowRequest request);
    
    KnowledgeArticle publishArticle(Long articleId, Long operatorId);
    
    KnowledgeArticle offlineArticle(Long articleId, Long operatorId);
    
    KnowledgeArticle returnToDraft(Long articleId, Long operatorId, WorkflowRequest request);
    
    boolean canTransition(ArticleStatus from, ArticleStatus to, String userRole);
    
    void recordOperation(Long articleId, String operation, String fromStatus, String toStatus, Long operatorId, String comment, String ipAddress);
    
    void saveVersion(Long articleId, Long editorId, String content, String changeSummary, String editReason);
}