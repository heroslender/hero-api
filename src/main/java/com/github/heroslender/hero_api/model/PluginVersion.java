package com.github.heroslender.hero_api.model;

import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "versions", itemRelation = "version")
public record PluginVersion(
        Long id,
        String pluginId,
        String tag,
        long releasedAt,
        String releaseTitle,
        String releaseNotes,
        String downloadUrl,
        int downloadCount
) {
    public PluginVersion(String pluginId, String tag, long releasedAt, String releaseTitle, String releaseNotes, String downloadUrl, int downloadCount) {
        this(null, pluginId, tag, releasedAt, releaseTitle, releaseNotes, downloadUrl, downloadCount);
    }
}
