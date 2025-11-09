package com.github.heroslender.hero_api.entity;

public enum Role {
    USER("USER"),
    ADMIN("ADMIN");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getName() {
        return role;
    }
}
