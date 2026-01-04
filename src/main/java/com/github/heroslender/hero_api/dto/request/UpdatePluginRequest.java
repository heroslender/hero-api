package com.github.heroslender.hero_api.dto.request;

import com.github.heroslender.hero_api.model.PluginVisibility;
import org.checkerframework.checker.nullness.qual.NonNull;

public record UpdatePluginRequest(
        String displayName,
        PluginVisibility visibility,
        Float price,
        Float promoPrice,
        String tagline,
        String description
) {
}
