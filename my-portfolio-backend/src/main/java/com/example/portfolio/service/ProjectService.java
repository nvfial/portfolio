package com.example.portfolio.service;

import com.example.portfolio.model.dto.ProjectDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 项目服务接口
 */
public interface ProjectService {

    /**
     * 获取所有项目
     */
    List<ProjectDTO> getAll();

    /**
     * 根据分类获取项目
     */
    List<ProjectDTO> getByCategory(String category);

    /**
     * 保存项目（异步）
     */
    CompletableFuture<ProjectDTO> save(ProjectDTO dto);

    /**
     * 根据ID获取项目
     */
    ProjectDTO getById(Long id);

    /**
     * 更新项目
     */
    ProjectDTO update(Long id, ProjectDTO dto);

    /**
     * 删除项目
     */
    void delete(Long id);
}
