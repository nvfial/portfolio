package com.example.portfolio.model.dto;

import java.time.LocalDateTime;

public class StoryDTO {
    
    private Long id;
    private Long characterId;
    private String chapterTitle;
    private Integer chapterOrder;
    private String contentType;
    private String content;
    private String mediaUrls;
    private String bgmUrl;
    private String coverImageUrl;
    private LocalDateTime createdAt;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCharacterId() { return characterId; }
    public void setCharacterId(Long characterId) { this.characterId = characterId; }
    
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
}