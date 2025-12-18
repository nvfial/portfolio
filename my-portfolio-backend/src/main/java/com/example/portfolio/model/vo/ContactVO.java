package com.example.portfolio.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 联系消息视图对象
 */
@Data
public class ContactVO {

    private Long id;
    private String name;
    private String email;
    private String subject;
    private String message;
    private LocalDateTime createdAt;
    
    // 可以添加额外的展示字段
    private String formattedDate;  // 格式化后的日期
    private Boolean isRead;        // 是否已读
}






