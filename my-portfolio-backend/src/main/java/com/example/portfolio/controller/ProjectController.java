package com.example.portfolio.controller;

import com.example.portfolio.model.dto.ProjectDTO;
import com.example.portfolio.model.dto.ProjectImportDTO;
import com.example.portfolio.service.ProjectAnalysisService;
import com.example.portfolio.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "API for managing projects")
public class ProjectController {

    @Autowired
    private ProjectService service;

    @Autowired
    private ProjectAnalysisService analysisService;

    @GetMapping
    @Operation(summary = "获取所有项目")
    public ResponseEntity<List<ProjectDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取项目")
    public ResponseEntity<ProjectDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "根据分类获取项目")
    public ResponseEntity<List<ProjectDTO>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(service.getByCategory(category));
    }

    @GetMapping("/featured")
    @Operation(summary = "获取精选项目")
    public ResponseEntity<List<ProjectDTO>> getFeatured() {
        return ResponseEntity.ok(service.getFeatured());
    }

    @PostMapping
    @Operation(summary = "创建新项目")
    public CompletableFuture<ResponseEntity<ProjectDTO>> create(@RequestBody ProjectDTO dto) {
        return service.save(dto).thenApply(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新项目")
    public ResponseEntity<ProjectDTO> update(@PathVariable Long id, @RequestBody ProjectDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除项目")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/import/github")
    @Operation(summary = "从GitHub仓库导入项目")
    public ResponseEntity<ProjectDTO> importFromGitHub(@RequestBody ProjectImportDTO importDTO) {
        ProjectDTO project = analysisService.analyzeFromGitHub(importDTO.getSource());
        if (importDTO.getCategory() != null) {
            project.setCategory(importDTO.getCategory());
            service.update(project.getId(), project);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }

    @PostMapping(value = "/import/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "从本地上传文件导入项目")
    public ResponseEntity<ProjectDTO> importFromUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", required = false) String category) {
        try {
            ProjectDTO project = analysisService.analyzeFromUpload(file.getBytes(), file.getOriginalFilename());
            if (category != null) {
                project.setCategory(category);
                service.update(project.getId(), project);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(project);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
    }

    @PostMapping("/{id}/reanalyze")
    @Operation(summary = "重新分析项目")
    public ResponseEntity<ProjectDTO> reAnalyze(@PathVariable Long id) {
        return ResponseEntity.ok(analysisService.reAnalyze(id));
    }
}