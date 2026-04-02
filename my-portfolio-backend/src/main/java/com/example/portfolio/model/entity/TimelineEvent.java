package com.example.portfolio.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "timeline_events")
public class TimelineEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id", nullable = false)
    private Character character;
    
    @Column(length = 50)
    private String eventYear;
    
    @Column(length = 200)
    private String eventTitle;
    
    @Column(columnDefinition = "TEXT")
    private String eventDesc;
    
    @Column(name = "event_image_url")
    private String eventImageUrl;
    
    @Column(length = 50)
    private String eventType;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
    
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
    
    public String getEventYear() { return eventYear; }
    public void setEventYear(String eventYear) { this.eventYear = eventYear; }
    
    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    
    public String getEventDesc() { return eventDesc; }
    public void setEventDesc(String eventDesc) { this.eventDesc = eventDesc; }
    
    public String getEventImageUrl() { return eventImageUrl; }
    public void setEventImageUrl(String eventImageUrl) { this.eventImageUrl = eventImageUrl; }
    
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}