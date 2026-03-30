package com.example.portfolio.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(nullable = false, length = 100)
    private String userName;

    @Column(length = 100)
    private String userEmail;

    @Column(name = "user_website", length = 200)
    private String userWebsite;

    @Column(name = "user_avatar", length = 500)
    private String userAvatar;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "target_type", length = 20)
    private String targetType = "general";

    @Column(name = "target_id")
    private Long targetId;

    @Column(length = 20)
    private String status = "pending";

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "likes_count")
    private Integer likesCount = 0;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
