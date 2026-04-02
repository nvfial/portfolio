package com.example.portfolio.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "characters")
public class Character {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 200)
    private String title;
    
    @Column(length = 100)
    private String cv;
    
    @Column(length = 500)
    private String shortDesc;
    
    @Column(length = 500)
    private String portraitUrl;
    
    @Column(length = 500)
    private String bgImageUrl;
    
    @Column(length = 500)
    private String voiceUrl;
    
    @Column(length = 500)
    private String fullBodyUrl;
    
    @Column(length = 500)
    private String sceneUrl;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "theme_color", length = 20)
    private String themeColor = "#c9a96a";
    
    @Column(name = "theme_color_secondary", length = 20)
    private String themeColorSecondary = "#8b7355";
    
    @Column(name = "background_type", length = 50)
    private String backgroundType = "gradient";
    
    @Column(name = "particle_effect", length = 50)
    private String particleEffect = "stars";
    
    @Column(name = "frame_style", length = 50)
    private String frameStyle = "classic";
    
    @Column(name = "font_family", length = 100)
    private String fontFamily = "serif";
    
    @Column(name = "entrance_animation", length = 50)
    private String entranceAnimation = "fade";
    
    @Column(name = "title_animation", length = 50)
    private String titleAnimation = "typewriter";
    
    @Column(name = "theme_preset", length = 50)
    private String themePreset = "golden";
    
    @Column(name = "custom_theme_config", columnDefinition = "JSON")
    private String customThemeConfig;
    
    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("chapterOrder ASC")
    private List<Story> stories = new ArrayList<>();
    
    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<TimelineEvent> timelineEvents = new ArrayList<>();
    
    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quote> quotes = new ArrayList<>();
    
    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<Media> mediaList = new ArrayList<>();
    
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
    
    public List<Story> getStories() { return stories; }
    public void setStories(List<Story> stories) { this.stories = stories; }
    
    public List<TimelineEvent> getTimelineEvents() { return timelineEvents; }
    public void setTimelineEvents(List<TimelineEvent> timelineEvents) { this.timelineEvents = timelineEvents; }
    
    public List<Quote> getQuotes() { return quotes; }
    public void setQuotes(List<Quote> quotes) { this.quotes = quotes; }
    
    public List<Media> getMediaList() { return mediaList; }
    public void setMediaList(List<Media> mediaList) { this.mediaList = mediaList; }
    
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
    
    public String getThemePreset() { return themePreset; }
    public void setThemePreset(String themePreset) { this.themePreset = themePreset; }
    
    public String getCustomThemeConfig() { return customThemeConfig; }
    public void setCustomThemeConfig(String customThemeConfig) { this.customThemeConfig = customThemeConfig; }
}