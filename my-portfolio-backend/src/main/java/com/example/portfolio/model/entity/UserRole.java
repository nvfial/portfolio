package com.example.portfolio.model.entity;

public enum UserRole {
    ADMIN("管理员"),
    REVIEWER("审核员"),
    AUTHOR("作者"),
    USER("普通用户"),
    GUEST("访客");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return switch (this) {
            case ADMIN -> 5;
            case REVIEWER -> 4;
            case AUTHOR -> 3;
            case USER -> 2;
            case GUEST -> 1;
        };
    }

    public boolean canReview() {
        return this == ADMIN || this == REVIEWER;
    }

    public boolean canManage() {
        return this == ADMIN;
    }
}