package com.example.portfolio.model.dto;

public class TimelineEventDTO {
    
    private Long id;
    private Long characterId;
    private String eventYear;
    private String eventTitle;
    private String eventDesc;
    private String eventImageUrl;
    private String eventType;
    private Integer sortOrder;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCharacterId() { return characterId; }
    public void setCharacterId(Long characterId) { this.characterId = characterId; }
    
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
}