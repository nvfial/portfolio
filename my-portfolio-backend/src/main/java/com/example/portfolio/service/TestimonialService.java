package com.example.portfolio.service;

import com.example.portfolio.model.dto.TestimonialDTO;

import java.util.List;

/**
 * 推荐信服务接口
 */
public interface TestimonialService {

    /**
     * 获取所有推荐信
     */
    List<TestimonialDTO> getAll();

    /**
     * 根据ID获取推荐信
     */
    TestimonialDTO getById(Long id);

    /**
     * 保存推荐信
     */
    TestimonialDTO save(TestimonialDTO dto);

    /**
     * 更新推荐信
     */
    TestimonialDTO update(Long id, TestimonialDTO dto);

    /**
     * 删除推荐信
     */
    void delete(Long id);
}
