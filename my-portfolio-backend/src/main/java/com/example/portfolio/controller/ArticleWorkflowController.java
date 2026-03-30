package com.example.portfolio.controller;

import com.example.portfolio.config.UserPrincipal;
import com.example.portfolio.model.dto.ArticleWorkflowDTO;
import com.example.portfolio.model.dto.WorkflowRequest;
import com.example.portfolio.model.entity.KnowledgeArticle;
import com.example.portfolio.service.ArticleWorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/author/articles")
@Tag(name = "Article Workflow", description = "文章工作流API")
public class ArticleWorkflowController {

    @Autowired
    private ArticleWorkflowService workflowService;

    @GetMapping("/{articleId}/workflow")
    @Operation(summary = "获取文章工作流状态")
    public ResponseEntity<ArticleWorkflowDTO> getWorkflowState(@PathVariable Long articleId) {
        ArticleWorkflowDTO workflow = workflowService.getWorkflowState(articleId);
        return ResponseEntity.ok(workflow);
    }

    @PostMapping("/{articleId}/submit")
    @Operation(summary = "提交文章审核")
    public ResponseEntity<Void> submitForReview(
            @PathVariable Long articleId,
            @RequestBody(required = false) WorkflowRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        workflowService.submitForReview(articleId, principal.getId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{articleId}/return-to-draft")
    @Operation(summary = "退回草稿")
    public ResponseEntity<Void> returnToDraft(
            @PathVariable Long articleId,
            @RequestBody(required = false) WorkflowRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        workflowService.returnToDraft(articleId, principal.getId(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{articleId}/save-version")
    @Operation(summary = "保存文章版本")
    public ResponseEntity<Void> saveVersion(
            @PathVariable Long articleId,
            @RequestBody WorkflowRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        workflowService.saveVersion(articleId, principal.getId(), 
            request.getComment(), null, request.getReason());
        return ResponseEntity.ok().build();
    }
}