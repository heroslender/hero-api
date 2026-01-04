package com.github.heroslender.hero_api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "plugins", itemRelation = "plugin")
public record Plugin(
        @NonNull String id,
        @NonNull String name,
        long ownerId,
        @NonNull PluginVisibility visibility,
        Float price,
        Float promoPrice,
        String tagline,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String description
) {
    public Plugin(String id, long ownerId) {
        this(id, id, ownerId, PluginVisibility.PUBLIC, 0F, 0F, "", "");
    }
}
