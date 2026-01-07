package com.github.heroslender.hero_api.dto.request;

import com.github.heroslender.hero_api.model.PluginVisibility;

public record UpdatePluginRequest(
        String name,
        PluginVisibility visibility,
        Float price,
        Float promoPrice,
        String tagline,
        String description
) {
}
