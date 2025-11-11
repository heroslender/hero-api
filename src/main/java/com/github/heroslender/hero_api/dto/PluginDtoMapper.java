package com.github.heroslender.hero_api.dto;

import com.github.heroslender.hero_api.entity.Plugin;
import com.github.heroslender.hero_api.entity.PluginVersion;

public class PluginDtoMapper {
    private PluginDtoMapper() {
    }

    public static PluginDTO toDto(Plugin plugin) {
        return new PluginDTO(
                plugin.getId(),
                plugin.getName()
        );
    }

    public static Plugin fromDto(PluginDTO dto) {
        return new Plugin(
                dto.id(),
                dto.name()
        );
    }

    public static PluginVersionDTO toDto(PluginVersion version) {
        return new PluginVersionDTO(
                version.getId(),
                version.getTag(),
                version.getReleasedAt(),
                version.getReleaseTitle(),
                version.getReleaseNotes(),
                version.getDownloadUrl(),
                version.getDownloadCount()
        );
    }

    public static PluginVersion fromDto(PluginVersionDTO dto, PluginDTO plugin) {
        return new PluginVersion(
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
