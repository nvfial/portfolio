package com.example.portfolio.controller;

import com.example.portfolio.model.dto.ResourceCategoryDTO;
import com.example.portfolio.model.entity.ResourceCategory;
import com.example.portfolio.repository.ResourceCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resources/categories")
@CrossOrigin(origins = "*")
public class ResourceCategoryController {
    
    @Autowired
    private ResourceCategoryRepository resourceCategoryRepository;
    
    @GetMapping
    public ResponseEntity<List<ResourceCategoryDTO>> getCategories() {
        List<ResourceCategory> categories = resourceCategoryRepository.findByIsActiveTrueOrderBySortOrderAsc();
        List<ResourceCategoryDTO> dtos = categories.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @PostMapping
    public ResponseEntity<ResourceCategoryDTO> create(@RequestBody ResourceCategoryDTO dto) {
        ResourceCategory category = new ResourceCategory();
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setDescription(dto.getDescription());
        category.setParentId(dto.getParentId());
        category.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        category.setIsActive(true);
        
        ResourceCategory saved = resourceCategoryRepository.save(category);
        return ResponseEntity.ok(toDTO(saved));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ResourceCategoryDTO> update(@PathVariable Long id, @RequestBody ResourceCategoryDTO dto) {
        ResourceCategory category = resourceCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        if (dto.getName() != null) category.setName(dto.getName());
        if (dto.getIcon() != null) category.setIcon(dto.getIcon());
        if (dto.getDescription() != null) category.setDescription(dto.getDescription());
        if (dto.getParentId() != null) category.setParentId(dto.getParentId());
        if (dto.getSortOrder() != null) category.setSortOrder(dto.getSortOrder());
        
        ResourceCategory saved = resourceCategoryRepository.save(category);
        return ResponseEntity.ok(toDTO(saved));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resourceCategoryRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    private ResourceCategoryDTO toDTO(ResourceCategory category) {
        ResourceCategoryDTO dto = new ResourceCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setIcon(category.getIcon());
        dto.setDescription(category.getDescription());
        dto.setParentId(category.getParentId());
        dto.setSortOrder(category.getSortOrder());
        dto.setIsActive(category.getIsActive());
        dto.setCreatedAt(category.getCreatedAt());
        return dto;
    }
}