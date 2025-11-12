package com.github.heroslender.hero_api.model;

import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "plugins", itemRelation = "plugin")
public record Plugin(
        Long id,
        String name
) {
}
