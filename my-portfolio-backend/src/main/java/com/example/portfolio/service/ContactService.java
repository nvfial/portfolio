package com.example.portfolio.service;

import com.example.portfolio.model.dto.ContactDTO;

import java.util.List;

/**
 * 联系消息服务接口
 */
public interface ContactService {

    /**
     * 获取所有联系消息
     */
    List<ContactDTO> getAll();

    /**
     * 根据ID获取联系消息
     */
    ContactDTO getById(Long id);

    /**
     * 保存联系消息
     */
    ContactDTO save(ContactDTO dto);

    /**
     * 更新联系消息
     */
    ContactDTO update(Long id, ContactDTO dto);

    /**
     * 删除联系消息
     */
    void delete(Long id);
}

