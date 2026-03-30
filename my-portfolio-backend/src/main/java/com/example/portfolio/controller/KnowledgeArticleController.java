package com.example.portfolio.controller;

import com.example.portfolio.model.dto.CreateArticleRequest;
import com.example.portfolio.model.dto.KnowledgeArticleDTO;
import com.example.portfolio.model.entity.ArticleStatus;
import com.example.portfolio.model.entity.KnowledgeArticle;
import com.example.portfolio.service.KnowledgeArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge/articles")
@Tag(name = "Knowledge Articles", description = "知识文章管理API")
public class KnowledgeArticleController {

    @Autowired
    private KnowledgeArticleService articleService;

    @GetMapping
    @Operation(summary = "获取文章列表（支持状态筛选）")
    public ResponseEntity<Page<KnowledgeArticleDTO>> getArticles(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        ArticleStatus articleStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                articleStatus = ArticleStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                // Ignore invalid status
            }
        }
        
        return ResponseEntity.ok(articleService.getArticlesByFilters(
            articleStatus, categoryId, authorId, search, page, size));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "获取分类下的所有已发布文章")
    public ResponseEntity<List<KnowledgeArticleDTO>> getArticlesByCategoryList(@PathVariable Long categoryId) {
        return ResponseEntity.ok(articleService.getArticlesByCategory(categoryId));
    }

    @GetMapping("/recent")
    @Operation(summary = "获取最新文章")
    public ResponseEntity<List<KnowledgeArticleDTO>> getRecentArticles(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(articleService.getRecentArticles(limit));
    }

    @GetMapping("/featured")
    @Operation(summary = "获取推荐文章")
    public ResponseEntity<List<KnowledgeArticleDTO>> getFeaturedArticles() {
        return ResponseEntity.ok(articleService.getFeaturedArticles());
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "通过slug获取文章详情")
    public ResponseEntity<KnowledgeArticleDTO> getArticleBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(articleService.getArticleBySlug(slug));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID获取文章详情")
    public ResponseEntity<KnowledgeArticleDTO> getArticleById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态获取文章")
    public ResponseEntity<Page<KnowledgeArticleDTO>> getArticlesByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ArticleStatus articleStatus = ArticleStatus.valueOf(status);
        return ResponseEntity.ok(articleService.getArticlesByStatus(articleStatus, page, size));
    }

    @GetMapping("/pending-review")
    @Operation(summary = "获取待审核文章")
    public ResponseEntity<List<KnowledgeArticleDTO>> getPendingReviewArticles() {
        return ResponseEntity.ok(articleService.getPendingReviewArticles());
    }

    @GetMapping("/count/status/{status}")
    @Operation(summary = "统计指定状态的文章数量")
    public ResponseEntity<Long> countByStatus(@PathVariable String status) {
        ArticleStatus articleStatus = ArticleStatus.valueOf(status);
        return ResponseEntity.ok(articleService.countByStatus(articleStatus));
    }

    @PostMapping
    @Operation(summary = "创建文章")
    public ResponseEntity<KnowledgeArticleDTO> createArticle(@RequestBody CreateArticleRequest request) {
        System.out.println("=== createArticle received ===");
        System.out.println("  title: " + request.getTitle());
        System.out.println("  slug: " + request.getSlug());
        System.out.println("  categoryId: " + request.getCategoryId());
        System.out.println("================================");
        
        KnowledgeArticle article = new KnowledgeArticle();
        article.setTitle(request.getTitle());
        article.setSlug(request.getSlug());
        article.setContent(request.getContent());
        article.setSummary(request.getSummary());
        article.setCoverImage(request.getCoverImage());
        article.setAuthorName(request.getAuthorName());
        article.setTags(request.getTags());
        article.setReadTime(request.getReadTime());
        article.setIsPublished(request.getIsPublished());
        article.setIsFeatured(request.getIsFeatured());
        if (request.getStatus() != null) {
            article.setStatus(ArticleStatus.valueOf(request.getStatus()));
        }
        article.setCategoryId(request.getCategoryId());
        article.setAuthorId(request.getAuthorId());
        
        System.out.println("=== Before service call ===");
        System.out.println("  article.getCategoryId(): " + article.getCategoryId());
        System.out.println("  article.getCategory(): " + article.getCategory());
        System.out.println("=============================");
        
        return ResponseEntity.ok(articleService.createArticle(article));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新文章")
    public ResponseEntity<KnowledgeArticleDTO> updateArticle(
            @PathVariable Long id, 
            @RequestBody KnowledgeArticle article) {
        return ResponseEntity.ok(articleService.updateArticle(id, article));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文章")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/view")
    @Operation(summary = "增加文章浏览量")
    public ResponseEntity<Void> incrementView(@PathVariable Long id) {
        articleService.incrementViewCount(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "增加文章点赞数")
    public ResponseEntity<Void> incrementLike(@PathVariable Long id) {
        articleService.incrementLikeCount(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "搜索文章")
    public ResponseEntity<Page<KnowledgeArticleDTO>> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(articleService.searchArticles(keyword, page, size));
    }
}