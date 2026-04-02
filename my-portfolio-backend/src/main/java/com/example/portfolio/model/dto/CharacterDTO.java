package com.example.portfolio.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CharacterDTO {
    
    private Long id;
    private String name;
    private String title;
    private String cv;
    private String shortDesc;
    private String portraitUrl;
    private String bgImageUrl;
    private String voiceUrl;
    private String fullBodyUrl;
    private String sceneUrl;
    private String bio;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private String themeColor;
    private String themeColorSecondary;
    private String backgroundType;
    private String particleEffect;
    private String frameStyle;
    private String fontFamily;
    private String entranceAnimation;
    private String titleAnimation;
    
    private List<StoryDTO> stories;
    private List<TimelineEventDTO> timelineEvents;
    private List<QuoteDTO> quotes;
    private List<MediaDTO> mediaList;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getCv() { return cv; }
    public void setCv(String cv) { this.cv = cv; }
    
    public String getShortDesc() { return shortDesc; }
    public void setShortDesc(String shortDesc) { this.shortDesc = shortDesc; }
    
    public String getPortraitUrl() { return portraitUrl; }
    public void setPortraitUrl(String portraitUrl) { this.portraitUrl = portraitUrl; }
    
    public String getBgImageUrl() { return bgImageUrl; }
    public void setBgImageUrl(String bgImageUrl) { this.bgImageUrl = bgImageUrl; }
    
    public String getVoiceUrl() { return voiceUrl; }
    public void setVoiceUrl(String voiceUrl) { this.voiceUrl = voiceUrl; }
    
    public String getFullBodyUrl() { return fullBodyUrl; }
    public void setFullBodyUrl(String fullBodyUrl) { this.fullBodyUrl = fullBodyUrl; }
    
    public String getSceneUrl() { return sceneUrl; }
    public void setSceneUrl(String sceneUrl) { this.sceneUrl = sceneUrl; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getThemeColor() { return themeColor; }
    public void setThemeColor(String themeColor) { this.themeColor = themeColor; }
    
    public String getThemeColorSecondary() { return themeColorSecondary; }
    public void setThemeColorSecondary(String themeColorSecondary) { this.themeColorSecondary = themeColorSecondary; }
    
    public String getBackgroundType() { return backgroundType; }
    public void setBackgroundType(String backgroundType) { this.backgroundType = backgroundType; }
    
    public String getParticleEffect() { return particleEffect; }
    public void setParticleEffect(String particleEffect) { this.particleEffect = particleEffect; }
    
    public String getFrameStyle() { return frameStyle; }
    public void setFrameStyle(String frameStyle) { this.frameStyle = frameStyle; }
    
    public String getFontFamily() { return fontFamily; }
    public void setFontFamily(String fontFamily) { this.fontFamily = fontFamily; }
    
    public String getEntranceAnimation() { return entranceAnimation; }
    public void setEntranceAnimation(String entranceAnimation) { this.entranceAnimation = entranceAnimation; }
    
    public String getTitleAnimation() { return titleAnimation; }
    public void setTitleAnimation(String titleAnimation) { this.titleAnimation = titleAnimation; }
    
    private String themePreset;
    private String customThemeConfig;
    
    public String getThemePreset() { return themePreset; }
    public void setThemePreset(String themePreset) { this.themePreset = themePreset; }
    
    public String getCustomThemeConfig() { return customThemeConfig; }
    public void setCustomThemeConfig(String customThemeConfig) { this.customThemeConfig = customThemeConfig; }
    
    public List<StoryDTO> getStories() { return stories; }
    public void setStories(List<StoryDTO> stories) { this.stories = stories; }
    
    public List<TimelineEventDTO> getTimelineEvents() { return timelineEvents; }
    public void setTimelineEvents(List<TimelineEventDTO> timelineEvents) { this.timelineEvents = timelineEvents; }
    
    public List<QuoteDTO> getQuotes() { return quotes; }
    public void setQuotes(List<QuoteDTO> quotes) { this.quotes = quotes; }
    
    public List<MediaDTO> getMediaList() { return mediaList; }
    public void setMediaList(List<MediaDTO> mediaList) { this.mediaList = mediaList; }
}