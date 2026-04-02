package com.example.portfolio.controller;

import com.example.portfolio.model.dto.ResourceCategoryDTO;
import com.example.portfolio.model.dto.ResourceDTO;
import com.example.portfolio.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resources")
@CrossOrigin(origins = "*")
public class ResourceController {
    
    @Autowired
    private ResourceService resourceService;
    
    @GetMapping
    public ResponseEntity<Page<ResourceDTO>> getResources(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Page<ResourceDTO> resources = resourceService.getResources(type, categoryId, keyword, page, size);
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ResourceDTO> getById(@PathVariable Long id) {
        ResourceDTO resource = resourceService.getById(id);
        return ResponseEntity.ok(resource);
    }
    
    @PostMapping
    public ResponseEntity<ResourceDTO> create(
            @RequestPart("data") ResourceDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        ResourceDTO created = resourceService.create(dto, file);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ResourceDTO> update(
            @PathVariable Long id,
            @RequestPart("data") ResourceDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        ResourceDTO updated = resourceService.update(id, dto, file);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resourceService.delete(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<ResourceCategoryDTO>> getCategories() {
        List<ResourceCategoryDTO> categories = resourceService.getCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/{id}/download")
    public ResponseEntity<Map<String, String>> getDownloadUrl(@PathVariable Long id) {
        ResourceDTO resource = resourceService.getDownloadUrl(id);
        Map<String, String> result = new HashMap<>();
        result.put("downloadUrl", resource.getCdnUrl());
        result.put("fileName", resource.getName());
        result.put("fileType", resource.getFileType());
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/{id}/record")
    public ResponseEntity<Void> recordDownload(@PathVariable Long id) {
        resourceService.incrementDownloadCount(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/popular")
    public ResponseEntity<Page<ResourceDTO>> getPopular(@RequestParam(defaultValue = "10") int limit) {
        Page<ResourceDTO> resources = resourceService.getPopular(limit);
        return ResponseEntity.ok(resources);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<Page<ResourceDTO>> getRecent(@RequestParam(defaultValue = "10") int limit) {
        Page<ResourceDTO> resources = resourceService.getRecent(limit);
        return ResponseEntity.ok(resources);
    }
}