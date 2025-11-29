package com.github.heroslender.hero_api.model;

public enum UserRole {
    USER("USER"),
    DEVELOPER("DEVELOPER"),
    ADMIN("ADMIN");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getName() {
        return role;
    }
}
