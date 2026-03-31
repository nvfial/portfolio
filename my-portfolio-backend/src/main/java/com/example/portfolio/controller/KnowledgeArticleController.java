package com.example.portfolio.controller;

import com.example.portfolio.model.dto.CreateArticleRequest;
import com.example.portfolio.model.dto.KnowledgeArticleDTO;
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
    @Operation(summary = "获取文章列表")
    public ResponseEntity<Page<KnowledgeArticleDTO>> getArticles(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(articleService.getArticlesByFilters(null, categoryId, null, search, page, size));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "获取分类下的所有文章")
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

    @PostMapping
    @Operation(summary = "创建文章")
    public ResponseEntity<KnowledgeArticleDTO> createArticle(@RequestBody CreateArticleRequest request) {
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
        article.setCategoryId(request.getCategoryId());
        article.setAuthorId(request.getAuthorId());
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