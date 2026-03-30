package com.example.portfolio.controller;

import com.example.portfolio.model.dto.KnowledgeCategoryDTO;
import com.example.portfolio.model.entity.KnowledgeCategory;
import com.example.portfolio.service.KnowledgeCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge/categories")
@Tag(name = "Knowledge Categories", description = "知识分类管理API")
public class KnowledgeCategoryController {

    @Autowired
    private KnowledgeCategoryService categoryService;

    @GetMapping
    @Operation(summary = "获取所有知识分类")
    public ResponseEntity<List<KnowledgeCategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/domain/{domainId}")
    @Operation(summary = "获取指定领域下的分类")
    public ResponseEntity<List<KnowledgeCategoryDTO>> getCategoriesByDomain(@PathVariable Long domainId) {
        return ResponseEntity.ok(categoryService.getCategoriesByDomain(domainId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "通过ID获取分类详情")
    public ResponseEntity<KnowledgeCategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @GetMapping("/domain/{domainId}/{slug}")
    @Operation(summary = "通过slug获取分类详情")
    public ResponseEntity<KnowledgeCategoryDTO> getCategoryBySlug(
            @PathVariable Long domainId, 
            @PathVariable String slug) {
        return ResponseEntity.ok(categoryService.getCategoryBySlug(domainId, slug));
    }

    @PostMapping
    @Operation(summary = "创建知识分类")
    public ResponseEntity<KnowledgeCategoryDTO> createCategory(@RequestBody KnowledgeCategory category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新知识分类")
    public ResponseEntity<KnowledgeCategoryDTO> updateCategory(
            @PathVariable Long id, 
            @RequestBody KnowledgeCategory category) {
        return ResponseEntity.ok(categoryService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除知识分类")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
