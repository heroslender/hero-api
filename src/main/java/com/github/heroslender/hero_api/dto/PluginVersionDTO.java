package com.github.heroslender.hero_api.dto;

import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "versions", itemRelation = "version")
public record PluginVersionDTO(
        Long id,
        Long pluginId,
        String tag,
        long releasedAt,
        String releaseTitle,
        String releaseNotes,
        String downloadUrl,
        int downloadCount
) {
    public PluginVersionDTO(Long pluginId,String tag, long releasedAt, String releaseTitle, String releaseNotes, String downloadUrl, int downloadCount) {
        this(null, pluginId, tag, releasedAt, releaseTitle, releaseNotes, downloadUrl, downloadCount);
    }
}
