package com.example.portfolio.service.impl;

import com.example.portfolio.model.dto.ArticleWorkflowDTO;
import com.example.portfolio.model.dto.WorkflowRequest;
import com.example.portfolio.model.entity.*;
import com.example.portfolio.repository.ArticleOperationRepository;
import com.example.portfolio.repository.ArticleVersionRepository;
import com.example.portfolio.repository.KnowledgeArticleRepository;
import com.example.portfolio.repository.UserRepository;
import com.example.portfolio.service.ArticleWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ArticleWorkflowServiceImpl implements ArticleWorkflowService {

    @Autowired
    private KnowledgeArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleVersionRepository versionRepository;

    @Autowired
    private ArticleOperationRepository operationRepository;

    @Override
    public ArticleWorkflowDTO getWorkflowState(Long articleId) {
        KnowledgeArticle article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        ArticleWorkflowDTO dto = new ArticleWorkflowDTO();
        dto.setArticleId(articleId);
        dto.setCurrentStatus(article.getStatus().name());
        dto.setStatusDescription(article.getStatus().getDescription());
        dto.setVersion(article.getVersion());
        dto.setSubmittedAt(article.getSubmittedAt());
        dto.setReviewedAt(article.getReviewedAt());
        dto.setReviewComment(article.getReviewComment());

        if (article.getAuthor() != null) {
            dto.setAuthorId(article.getAuthor().getId());
            dto.setAuthorName(article.getAuthor().getDisplayName());
        }
        if (article.getReviewer() != null) {
            dto.setReviewerId(article.getReviewer().getId());
            dto.setReviewerName(article.getReviewer().getDisplayName());
        }

        List<ArticleWorkflowDTO.StatusTransition> transitions = buildTransitions(article.getStatus());
        dto.setAvailableTransitions(transitions);

        return dto;
    }

    @Override
    public KnowledgeArticle submitForReview(Long articleId, Long authorId, WorkflowRequest request) {
        KnowledgeArticle article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        if (article.getAuthor() == null || !article.getAuthor().getId().equals(authorId)) {
            throw new RuntimeException("只有作者可以提交审核");
        }

        if (article.getStatus() != ArticleStatus.DRAFT && 
            article.getStatus() != ArticleStatus.EDITING && 
            article.getStatus() != ArticleStatus.REJECTED) {
            throw new RuntimeException("当前状态不允许提交审核");
        }

        validateArticleContent(article);

        article.setStatus(ArticleStatus.PENDING_REVIEW);
        article.setSubmittedAt(LocalDateTime.now());
        
        KnowledgeArticle saved = articleRepository.save(article);
        
        recordOperation(articleId, "SUBMIT_FOR_REVIEW", 
            article.getStatus().name(), ArticleStatus.PENDING_REVIEW.name(), 
            authorId, request != null ? request.getComment() : null, null);

        return saved;
    }

    @Override
    public KnowledgeArticle approveArticle(Long articleId, Long reviewerId, WorkflowRequest request) {
        KnowledgeArticle article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        if (article.getStatus() != ArticleStatus.PENDING_REVIEW) {
            throw new RuntimeException("文章不在待审核状态");
        }

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("审核员不存在"));

        if (!reviewer.hasRole("ADMIN") && !reviewer.hasRole("REVIEWER")) {
            throw new RuntimeException("只有管理员或审核员可以审核文章");
        }

        article.setStatus(ArticleStatus.PUBLISHED);
        article.setIsPublished(true);
        article.setReviewer(reviewer);
        article.setReviewedAt(LocalDateTime.now());
        article.setPublishedAt(LocalDateTime.now());
        if (request != null && request.getComment() != null) {
            article.setReviewComment(request.getComment());
        }

        KnowledgeArticle saved = articleRepository.save(article);
        
        recordOperation(articleId, "APPROVE", 
            ArticleStatus.PENDING_REVIEW.name(), ArticleStatus.PUBLISHED.name(), 
            reviewerId, request != null ? request.getComment() : null, null);

        return saved;
    }

    @Override
    public KnowledgeArticle rejectArticle(Long articleId, Long reviewerId, WorkflowRequest request) {
        KnowledgeArticle article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        if (article.getStatus() != ArticleStatus.PENDING_REVIEW) {
            throw new RuntimeException("文章不在待审核状态");
        }

        if (request == null || request.getComment() == null || request.getComment().trim().isEmpty()) {
            throw new RuntimeException("驳回时必须填写原因");
        }

        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("审核员不存在"));

        if (!reviewer.hasRole("ADMIN") && !reviewer.hasRole("REVIEWER")) {
            throw new RuntimeException("只有管理员或审核员可以审核文章");
        }

        article.setStatus(ArticleStatus.REJECTED);
        article.setReviewer(reviewer);
        article.setReviewedAt(LocalDateTime.now());
        article.setReviewComment(request.getComment());

        KnowledgeArticle saved = articleRepository.save(article);
        
        recordOperation(articleId, "REJECT", 
            ArticleStatus.PENDING_REVIEW.name(), ArticleStatus.REJECTED.name(), 
            reviewerId, request.getComment(), null);

        return saved;
    }

    @Override
    public KnowledgeArticle publishArticle(Long articleId, Long operatorId) {
        KnowledgeArticle article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        if (article.getStatus() != ArticleStatus.OFFLINE) {
            throw new RuntimeException("只有已下线的文章可以重新发布");
        }

        article.setStatus(ArticleStatus.PUBLISHED);
        article.setIsPublished(true);
        article.setPublishedAt(LocalDateTime.now());

        KnowledgeArticle saved = articleRepository.save(article);
        
        recordOperation(articleId, "PUBLISH", 
            ArticleStatus.OFFLINE.name(), ArticleStatus.PUBLISHED.name(), 
            operatorId, "重新发布", null);

        return saved;
    }

    @Override
    public KnowledgeArticle offlineArticle(Long articleId, Long operatorId) {
        KnowledgeArticle article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        if (article.getStatus() != ArticleStatus.PUBLISHED) {
            throw new RuntimeException("只有已发布的文章可以下线");
        }

        article.setStatus(ArticleStatus.OFFLINE);
        article.setIsPublished(false);

        KnowledgeArticle saved = articleRepository.save(article);
        
        recordOperation(articleId, "OFFLINE", 
            ArticleStatus.PUBLISHED.name(), ArticleStatus.OFFLINE.name(), 
            operatorId, null, null);

        return saved;
    }

    @Override
    public KnowledgeArticle returnToDraft(Long articleId, Long operatorId, WorkflowRequest request) {
        KnowledgeArticle article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));

        if (article.getStatus() != ArticleStatus.REJECTED) {
            throw new RuntimeException("只有被驳回的文章可以退回草稿");
        }

        article.setStatus(ArticleStatus.DRAFT);
        article.setIsPublished(false);

        KnowledgeArticle saved = articleRepository.save(article);
        
        recordOperation(articleId, "RETURN_TO_DRAFT", 
            article.getStatus().name(), ArticleStatus.DRAFT.name(), 
            operatorId, request != null ? request.getComment() : null, null);

        return saved;
    }

    @Override
    public boolean canTransition(ArticleStatus from, ArticleStatus to, String userRole) {
        return from.canTransitTo(to);
    }

    @Override
    public void recordOperation(Long articleId, String operation, String fromStatus, String toStatus, 
                               Long operatorId, String comment, String ipAddress) {
        KnowledgeArticle article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        User operator = userRepository.findById(operatorId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        ArticleOperation op = new ArticleOperation();
        op.setArticle(article);
        op.setOperation(operation);
        op.setFromStatus(fromStatus);
        op.setToStatus(toStatus);
        op.setOperator(operator);
        op.setComment(comment);
        op.setIpAddress(ipAddress);

        operationRepository.save(op);
    }

    @Override
    public void saveVersion(Long articleId, Long editorId, String content, String changeSummary, String editReason) {
        KnowledgeArticle article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        User editor = userRepository.findById(editorId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Integer maxVersion = versionRepository.findMaxVersionByArticleId(articleId);
        int newVersion = (maxVersion == null ? 0 : maxVersion) + 1;

        ArticleVersion version = new ArticleVersion();
        version.setArticle(article);
        version.setVersion(newVersion);
        version.setContent(content);
        version.setChangeSummary(changeSummary);
        version.setEditor(editor);
        version.setEditReason(editReason);

        versionRepository.save(version);

        article.setVersion(newVersion);
        articleRepository.save(article);
    }

    private List<ArticleWorkflowDTO.StatusTransition> buildTransitions(ArticleStatus currentStatus) {
        List<ArticleWorkflowDTO.StatusTransition> transitions = new ArrayList<>();

        switch (currentStatus) {
            case DRAFT, EDITING -> {
                addTransition(transitions, ArticleStatus.PENDING_REVIEW, "提交审核", "AUTHOR", true);
            }
            case PENDING_REVIEW -> {
                addTransition(transitions, ArticleStatus.PUBLISHED, "通过审核", "REVIEWER", true);
                addTransition(transitions, ArticleStatus.REJECTED, "驳回", "REVIEWER", true);
            }
            case PUBLISHED -> {
                addTransition(transitions, ArticleStatus.OFFLINE, "下线", "ADMIN", true);
            }
            case REJECTED -> {
                addTransition(transitions, ArticleStatus.DRAFT, "退回草稿", "AUTHOR", true);
            }
            case OFFLINE -> {
                addTransition(transitions, ArticleStatus.PUBLISHED, "重新发布", "ADMIN", true);
            }
        }

        return transitions;
    }

    private void addTransition(List<ArticleWorkflowDTO.StatusTransition> transitions, 
                               ArticleStatus target, String action, String role, boolean allowed) {
        ArticleWorkflowDTO.StatusTransition t = new ArticleWorkflowDTO.StatusTransition();
        t.setTargetStatus(target.name());
        t.setTargetDescription(target.getDescription());
        t.setAction(action);
        t.setRequiredRole(role);
        t.setAllowed(allowed);
        transitions.add(t);
    }

    private void validateArticleContent(KnowledgeArticle article) {
        if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
            throw new RuntimeException("标题不能为空");
        }
        if (article.getTitle().length() > 200) {
            throw new RuntimeException("标题不能超过200字符");
        }
        if (article.getContent() == null || article.getContent().trim().isEmpty()) {
            throw new RuntimeException("内容不能为空");
        }
        if (article.getContent().length() < 100) {
            throw new RuntimeException("内容不能少于100字符");
        }
    }
}