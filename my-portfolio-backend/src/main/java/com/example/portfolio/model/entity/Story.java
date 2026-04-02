package com.example.portfolio.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stories")
public class Story {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private Character character;
    
    @Column(length = 200)
    private String chapterTitle;
    
    @Column(name = "chapter_order")
    private Integer chapterOrder;
    
    @Column(length = 50)
    private String contentType;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(columnDefinition = "JSON")
    private String mediaUrls;
    
    @Column(length = 500)
    private String bgmUrl;
    
    @Column(name = "cover_image_url")
    private String coverImageUrl;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Character getCharacter() { return character; }
    public void setCharacter(Character character) { this.character = character; }
    
    public String getChapterTitle() { return chapterTitle; }
    public void setChapterTitle(String chapterTitle) { this.chapterTitle = chapterTitle; }
    
    public Integer getChapterOrder() { return chapterOrder; }
    public void setChapterOrder(Integer chapterOrder) { this.chapterOrder = chapterOrder; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getMediaUrls() { return mediaUrls; }
    public void setMediaUrls(String mediaUrls) { this.mediaUrls = mediaUrls; }
    
    public String getBgmUrl() { return bgmUrl; }
    public void setBgmUrl(String bgmUrl) { this.bgmUrl = bgmUrl; }
    
    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}