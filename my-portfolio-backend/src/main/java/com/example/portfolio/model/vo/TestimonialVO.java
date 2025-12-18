package com.example.portfolio.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 推荐信视图对象
 */
@Data
public class TestimonialVO {

    private Long id;
    private String author;
    private String content;
    private LocalDateTime createdAt;
    
    // 可以添加额外的展示字段
    private String formattedDate;  // 格式化后的日期
    private String avatar;         // 头像URL
}






