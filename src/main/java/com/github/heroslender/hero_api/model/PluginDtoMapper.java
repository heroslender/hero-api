package com.github.heroslender.hero_api.model;

import com.github.heroslender.hero_api.database.entity.PluginEntity;
import com.github.heroslender.hero_api.database.entity.PluginVersionEntity;

public class PluginDtoMapper {
    private PluginDtoMapper() {
    }

    public static Plugin toDto(PluginEntity plugin) {
        return new Plugin(
                plugin.getId(),
                plugin.getName()
        );
    }

    public static PluginEntity fromDto(Plugin dto) {
        return new PluginEntity(
                dto.id(),
                dto.name()
        );
    }

    public static PluginVersion toDto(PluginVersionEntity version) {
        return new PluginVersion(
                version.getId(),
                version.getPlugin().getId(),
                version.getTag(),
                version.getReleasedAt(),
                version.getReleaseTitle(),
                version.getReleaseNotes(),
                version.getDownloadUrl(),
                version.getDownloadCount()
        );
    }

    public static PluginVersionEntity fromDto(PluginVersion dto, Plugin plugin) {
        return new PluginVersionEntity(
                dto.id(),
                fromDto(plugin),
                dto.tag(),
                dto.releasedAt(),
                dto.releaseTitle(),
                dto.releaseNotes(),
                dto.downloadUrl(),
                dto.downloadCount()
        );
    }
}
