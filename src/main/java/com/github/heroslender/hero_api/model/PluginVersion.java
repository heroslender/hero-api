package com.github.heroslender.hero_api.model;

import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "versions", itemRelation = "version")
public record PluginVersion(
        Long id,
        Long pluginId,
        String tag,
        long releasedAt,
        String releaseTitle,
        String releaseNotes,
        String downloadUrl,
        int downloadCount
) {
    public PluginVersion(Long pluginId, String tag, long releasedAt, String releaseTitle, String releaseNotes, String downloadUrl, int downloadCount) {
        this(null, pluginId, tag, releasedAt, releaseTitle, releaseNotes, downloadUrl, downloadCount);
    }
}
