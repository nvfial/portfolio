package com.example.portfolio.model.dto;

public class QuoteDTO {
    
    private Long id;
    private Long characterId;
    private String quoteText;
    private String quoteAuthor;
    private String context;
    private Boolean isFeatured;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCharacterId() { return characterId; }
    public void setCharacterId(Long characterId) { this.characterId = characterId; }
    
    public String getQuoteText() { return quoteText; }
    public void setQuoteText(String quoteText) { this.quoteText = quoteText; }
    
    public String getQuoteAuthor() { return quoteAuthor; }
    public void setQuoteAuthor(String quoteAuthor) { this.quoteAuthor = quoteAuthor; }
    
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    
    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }
}