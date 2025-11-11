package com.github.heroslender.hero_api.dto;

public record NewPluginVersionDto(
        String releaseTitle,
        String releaseNotes,
        String downloadUrl
) {
}
