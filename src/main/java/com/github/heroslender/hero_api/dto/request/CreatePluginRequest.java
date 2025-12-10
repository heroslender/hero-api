package com.github.heroslender.hero_api.dto.request;

public record CreatePluginRequest(
        String name,
        String displayName,
        String description
) {
}
