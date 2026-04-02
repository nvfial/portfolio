package com.example.portfolio.service.impl;

import com.example.portfolio.model.dto.ResourceCategoryDTO;
import com.example.portfolio.model.dto.ResourceDTO;
import com.example.portfolio.model.entity.DownloadLog;
import com.example.portfolio.model.entity.Resource;
import com.example.portfolio.model.entity.ResourceCategory;
import com.example.portfolio.repository.DownloadLogRepository;
import com.example.portfolio.repository.ResourceCategoryRepository;
import com.example.portfolio.repository.ResourceRepository;
import com.example.portfolio.service.ResourceService;
import com.example.portfolio.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements ResourceService {
    
    @Autowired
    private ResourceRepository resourceRepository;
    
    @Autowired
    private ResourceCategoryRepository resourceCategoryRepository;
    
    @Autowired
    private DownloadLogRepository downloadLogRepository;
    
    @Autowired
    private StorageService storageService;
    
    @Override
    public Page<ResourceDTO> getResources(String type, Long categoryId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Resource> resources;
        
        if (keyword != null && !keyword.isEmpty()) {
            resources = resourceRepository.search(keyword, pageable);
        } else if (type != null && !type.isEmpty()) {
            resources = resourceRepository.findByTypeAndIsActiveTrue(type, pageable);
        } else if (categoryId != null) {
            resources = resourceRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable);
        } else {
            resources = resourceRepository.findByIsActiveTrue(pageable);
        }
        
        return resources.map(this::toDTO);
    }
    
    @Override
    public ResourceDTO getById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        
        resource.setViewCount(resource.getViewCount() + 1);
        resourceRepository.save(resource);
        
        return toDTO(resource);
    }
    
    @Override
    @Transactional
    public ResourceDTO create(ResourceDTO dto, MultipartFile file) {
        Resource resource = new Resource();
        
        if (file != null && !file.isEmpty()) {
            String storagePath = storageService.upload(file);
            String cdnUrl = storageService.getUrl(storagePath);
            String fileType = getFileType(file.getOriginalFilename());
            String previewUrl = storageService.getPreviewUrl(storagePath, fileType);
            
            resource.setStoragePath(storagePath);
            resource.setCdnUrl(cdnUrl);
            resource.setPreviewUrl(previewUrl);
            resource.setFileType(fileType);
            resource.setFileSize(file.getSize());
            
            if (previewUrl != null) {
                resource.setThumbnailUrl(cdnUrl);
            }
        }
        
        resource.setName(dto.getName());
        resource.setDescription(dto.getDescription());
        resource.setCategoryId(dto.getCategoryId());
        resource.setType(dto.getType());
        resource.setIsPremium(dto.getIsPremium() != null ? dto.getIsPremium() : false);
        resource.setRequiredPoints(dto.getRequiredPoints() != null ? dto.getRequiredPoints() : 0);
        resource.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        resource.setIsActive(true);
        resource.setCreatedBy(dto.getCreatedBy());
        
        Resource saved = resourceRepository.save(resource);
        return toDTO(saved);
    }
    
    @Override
    @Transactional
    public ResourceDTO update(Long id, ResourceDTO dto, MultipartFile file) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        
        if (file != null && !file.isEmpty()) {
            if (resource.getStoragePath() != null) {
                storageService.delete(resource.getStoragePath());
            }
            
            String storagePath = storageService.upload(file);
            String cdnUrl = storageService.getUrl(storagePath);
            String fileType = getFileType(file.getOriginalFilename());
            String previewUrl = storageService.getPreviewUrl(storagePath, fileType);
            
            resource.setStoragePath(storagePath);
            resource.setCdnUrl(cdnUrl);
            resource.setPreviewUrl(previewUrl);
            resource.setFileType(fileType);
            resource.setFileSize(file.getSize());
            
            if (previewUrl != null) {
                resource.setThumbnailUrl(cdnUrl);
            }
        }
        
        if (dto.getName() != null) resource.setName(dto.getName());
        if (dto.getDescription() != null) resource.setDescription(dto.getDescription());
        if (dto.getCategoryId() != null) resource.setCategoryId(dto.getCategoryId());
        if (dto.getType() != null) resource.setType(dto.getType());
        if (dto.getIsPremium() != null) resource.setIsPremium(dto.getIsPremium());
        if (dto.getRequiredPoints() != null) resource.setRequiredPoints(dto.getRequiredPoints());
        if (dto.getSortOrder() != null) resource.setSortOrder(dto.getSortOrder());
        
        Resource saved = resourceRepository.save(resource);
        return toDTO(saved);
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        
        if (resource.getStoragePath() != null) {
            storageService.delete(resource.getStoragePath());
        }
        
        resourceRepository.delete(resource);
    }
    
    @Override
    public List<ResourceCategoryDTO> getCategories() {
        return resourceCategoryRepository.findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(this::toCategoryDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public ResourceDTO getDownloadUrl(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        
        if (!resource.getIsActive()) {
            throw new RuntimeException("Resource is not available");
        }
        
        return toDTO(resource);
    }
    
    @Override
    public Page<ResourceDTO> getPopular(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return resourceRepository.findPopular(pageable).map(this::toDTO);
    }
    
    @Override
    public Page<ResourceDTO> getRecent(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return resourceRepository.findRecent(pageable).map(this::toDTO);
    }
    
    @Override
    @Transactional
    public void incrementDownloadCount(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        resource.setDownloadCount(resource.getDownloadCount() + 1);
        resourceRepository.save(resource);
    }
    
    private ResourceDTO toDTO(Resource resource) {
        ResourceDTO dto = new ResourceDTO();
        dto.setId(resource.getId());
        dto.setName(resource.getName());
        dto.setDescription(resource.getDescription());
        dto.setCategoryId(resource.getCategoryId());
        dto.setType(resource.getType());
        dto.setFileType(resource.getFileType());
        dto.setFileSize(resource.getFileSize());
        dto.setStoragePath(resource.getStoragePath());
        dto.setCdnUrl(resource.getCdnUrl());
        dto.setPreviewUrl(resource.getPreviewUrl());
        dto.setThumbnailUrl(resource.getThumbnailUrl());
        dto.setDownloadCount(resource.getDownloadCount());
        dto.setViewCount(resource.getViewCount());
        dto.setIsPremium(resource.getIsPremium());
        dto.setRequiredPoints(resource.getRequiredPoints());
        dto.setIsActive(resource.getIsActive());
        dto.setSortOrder(resource.getSortOrder());
        dto.setCreatedBy(resource.getCreatedBy());
        dto.setCreatedAt(resource.getCreatedAt());
        dto.setUpdatedAt(resource.getUpdatedAt());
        
        if (resource.getCategoryId() != null) {
            resourceCategoryRepository.findById(resource.getCategoryId())
                    .ifPresent(cat -> dto.setCategoryName(cat.getName()));
        }
        
        return dto;
    }
    
    private ResourceCategoryDTO toCategoryDTO(ResourceCategory category) {
        ResourceCategoryDTO dto = new ResourceCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setIcon(category.getIcon());
        dto.setDescription(category.getDescription());
        dto.setParentId(category.getParentId());
        dto.setSortOrder(category.getSortOrder());
        dto.setIsActive(category.getIsActive());
        dto.setCreatedAt(category.getCreatedAt());
        
        Long count = resourceRepository.countByCategoryId(category.getId());
        dto.setResourceCount(count != null ? count.intValue() : 0);
        
        return dto;
    }
    
    private String getFileType(String filename) {
        if (filename == null) return null;
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0) {
            return filename.substring(lastDot + 1).toLowerCase();
        }
        return null;
    }
}