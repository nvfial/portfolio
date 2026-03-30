package com.example.portfolio.model.entity;

public enum ArticleStatus {
    DRAFT("草稿"),
    EDITING("编辑中"),
    PENDING_REVIEW("待审核"),
    PUBLISHED("已发布"),
    REJECTED("已驳回"),
    OFFLINE("已下线");

    private final String description;

    ArticleStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canTransitTo(ArticleStatus target) {
        return switch (this) {
            case DRAFT, EDITING -> target == PENDING_REVIEW;
            case PENDING_REVIEW -> target == PUBLISHED || target == REJECTED;
            case PUBLISHED -> target == OFFLINE;
            case REJECTED -> target == DRAFT || target == EDITING;
            case OFFLINE -> target == PUBLISHED;
        };
    }

    public boolean isEditable() {
        return this == DRAFT || this == EDITING || this == REJECTED;
    }

    public boolean isVisible() {
        return this == PUBLISHED;
    }
}