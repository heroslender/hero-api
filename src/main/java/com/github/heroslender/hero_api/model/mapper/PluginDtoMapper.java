package com.github.heroslender.hero_api.model.mapper;

import com.github.heroslender.hero_api.database.entity.PluginEntity;
import com.github.heroslender.hero_api.database.entity.PluginVersionEntity;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginVersion;

public class PluginDtoMapper {
    private PluginDtoMapper() {
    }

    public static Plugin toDto(PluginEntity plugin) {
        return new Plugin(
                plugin.getName(),
                plugin.getOwner().getId(),
                plugin.getVisibility(),
                plugin.getDisplayName(),
                plugin.getDescrition()
        );
    }

    public static PluginEntity fromDto(Plugin dto) {
        return new PluginEntity(
                dto.name(),
                dto.visibility(),
                dto.displayName(),
                dto.description()
        );
    }

    public static PluginVersion toDto(PluginVersionEntity version) {
        return new PluginVersion(
                version.getId(),
                version.getPlugin().getName(),
                version.getTag(),
                version.getReleasedAt(),
                version.getReleaseTitle(),
                version.getReleaseNotes(),
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
                dto.downloadCount()
        );
    }
}
