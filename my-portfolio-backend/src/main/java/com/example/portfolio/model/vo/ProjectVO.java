package com.example.portfolio.model.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 项目视图对象（View Object）
 * 用于前端展示，可能包含额外的展示字段
 */
@Data
public class ProjectVO {

    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String category;
    private List<String> tags;
    private String link;
    private String github;
    private LocalDateTime createdAt;
    
    // 可以添加额外的展示字段
    private String categoryLabel;  // 分类标签
    private Integer viewCount;      // 浏览次数（示例）
}






