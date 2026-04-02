package com.example.portfolio.service;

import com.example.portfolio.model.dto.ResourceCategoryDTO;
import com.example.portfolio.model.dto.ResourceDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {
    Page<ResourceDTO> getResources(String type, Long categoryId, String keyword, int page, int size);
    ResourceDTO getById(Long id);
    ResourceDTO create(ResourceDTO dto, MultipartFile file);
    ResourceDTO update(Long id, ResourceDTO dto, MultipartFile file);
    void delete(Long id);
    List<ResourceCategoryDTO> getCategories();
    ResourceDTO getDownloadUrl(Long id);
    Page<ResourceDTO> getPopular(int limit);
    Page<ResourceDTO> getRecent(int limit);
    void incrementDownloadCount(Long id);
}