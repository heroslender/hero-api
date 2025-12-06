package com.github.heroslender.hero_api.model;

import java.util.UUID;

public record PluginLicence(
        UUID id,
        long createdAt,
        long duration,
        String pluginId,
        long ownerId
) {
}
