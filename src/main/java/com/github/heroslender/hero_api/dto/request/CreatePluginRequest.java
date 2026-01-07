package com.github.heroslender.hero_api.dto.request;

import com.github.heroslender.hero_api.model.PluginVisibility;
import org.checkerframework.checker.nullness.qual.NonNull;

public record CreatePluginRequest(
        @NonNull String id,
        String name,
        @NonNull PluginVisibility visibility,
        Float price,
        String tagline,
        String description
) {
}
