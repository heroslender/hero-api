package com.github.heroslender.hero_api.dto;

import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "plugins", itemRelation = "plugin")
public record PluginDTO(
        Long id,
        String name
) {
}
