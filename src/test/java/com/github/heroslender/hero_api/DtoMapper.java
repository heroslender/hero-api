package com.github.heroslender.hero_api;

import com.github.heroslender.hero_api.database.entity.PluginEntity;
import com.github.heroslender.hero_api.database.entity.PluginLicenceEntity;
import com.github.heroslender.hero_api.database.entity.PluginVersionEntity;
import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginLicence;
import com.github.heroslender.hero_api.model.PluginVersion;

public class DtoMapper {
    private DtoMapper() {
    }

    public static Plugin toDto(PluginEntity plugin) {
        return new Plugin(
                plugin.getId(),
                plugin.getName(),
                plugin.getOwner().getId(),
                plugin.getVisibility(),
                plugin.getPrice(),
                plugin.getPromoPrice(),
                plugin.getTagline(),
                plugin.getDescription()
        );
    }

    public static PluginEntity fromDto(Plugin dto) {

        return new PluginEntity(
                dto.id(),
                dto.name(),
                new UserEntity(dto.ownerId()),
                dto.visibility(),
                dto.price(),
                dto.promoPrice(),
                dto.tagline(),
                dto.description(),
                null
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

    public static PluginLicence toDto(PluginLicenceEntity entity) {
        return new PluginLicence(
                entity.getId(),
                entity.getCreatedAt(),
                entity.getDuration(),
                entity.getPlugin().getId(),
                entity.getOwner().getId()
        );
    }

    public static PluginLicenceEntity fromDto(PluginLicence dto) {
        PluginEntity plugin = new PluginEntity();
        plugin.setId(dto.pluginId());

        UserEntity owner = new UserEntity();
        owner.setId(dto.ownerId());

        return new PluginLicenceEntity(
                dto.id(),
                dto.createdAt(),
                dto.duration(),
                plugin,
                owner
        );
    }
}
