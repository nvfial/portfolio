package com.example.portfolio.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "visit_logs", indexes = {
    @Index(name = "idx_ip", columnList = "ip"),
    @Index(name = "idx_created_at", columnList = "createdAt"),
    @Index(name = "idx_path", columnList = "path")
})
@Data
@EntityListeners(AuditingEntityListener.class)
public class VisitLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ip;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    private String referrer;

    private String language;

    @Column(name = "screen_resolution")
    private String screenResolution;

    @Column(nullable = false)
    private String path;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}






