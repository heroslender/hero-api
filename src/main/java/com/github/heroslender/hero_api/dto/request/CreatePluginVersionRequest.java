package com.github.heroslender.hero_api.dto.request;

public record CreatePluginVersionRequest(
        String releaseTitle,
        String releaseNotes
) {
}
