package com.example.portfolio.controller;

import com.example.portfolio.config.UserPrincipal;
import com.example.portfolio.model.dto.ArticleWorkflowDTO;
import com.example.portfolio.model.dto.WorkflowRequest;
import com.example.portfolio.service.ArticleWorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviewer/articles")
@Tag(name = "Article Review", description = "文章审核API")
public class ArticleReviewController {

    @Autowired
    private ArticleWorkflowService workflowService;

    @GetMapping("/{articleId}/workflow")
    @Operation(summary = "获取文章工作流状态")
    public ResponseEntity<ArticleWorkflowDTO> getWorkflowState(@PathVariable Long articleId) {
        ArticleWorkflowDTO workflow = workflowService.getWorkflowState(articleId);
        return ResponseEntity.ok(workflow);
    }

    @PostMapping("/{articleId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "通过文章审核")
    public ResponseEntity<Void> approveArticle(
            @PathVariable Long articleId,
            @RequestBody(required = false) WorkflowRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        workflowService.approveArticle(articleId, principal.getId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{articleId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'REVIEWER')")
    @Operation(summary = "驳回文章")
    public ResponseEntity<Void> rejectArticle(
            @PathVariable Long articleId,
            @RequestBody WorkflowRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        workflowService.rejectArticle(articleId, principal.getId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{articleId}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "发布文章")
    public ResponseEntity<Void> publishArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal UserPrincipal principal) {
        workflowService.publishArticle(articleId, principal.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{articleId}/offline")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "下线文章")
    public ResponseEntity<Void> offlineArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal UserPrincipal principal) {
        workflowService.offlineArticle(articleId, principal.getId());
        return ResponseEntity.ok().build();
    }
}