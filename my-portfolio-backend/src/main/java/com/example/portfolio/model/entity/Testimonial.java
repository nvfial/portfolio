package com.example.portfolio.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "testimonials")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Testimonial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}






