package com.github.heroslender.hero_api.database.entity;

public enum UserRole {
    USER("USER"),
    ADMIN("ADMIN");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getName() {
        return role;
    }
}
