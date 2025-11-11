package com.github.heroslender.hero_api.dto;

import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "versions", itemRelation = "version")
public record PluginVersionDTO(
        Long id,
        String tag,
        long releasedAt,
        String releaseTitle,
        String releaseNotes,
        String downloadUrl,
        int downloadCount
) {
    public PluginVersionDTO(String tag, long releasedAt, String releaseTitle, String releaseNotes, String downloadUrl, int downloadCount) {
        this(null, tag, releasedAt, releaseTitle, releaseNotes, downloadUrl, downloadCount);
    }
}
