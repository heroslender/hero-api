package com.github.heroslender.hero_api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "plugins", itemRelation = "plugin")
public record Plugin(
        String name,
        String displayName,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String description
) {
}
