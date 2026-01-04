package com.github.heroslender.hero_api.model.mapper;

import com.github.heroslender.hero_api.database.entity.PluginEntity;
import com.github.heroslender.hero_api.database.entity.PluginLicenceEntity;
import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.model.PluginLicence;

public class PluginLicenceDtoMapper {

    private PluginLicenceDtoMapper() {
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
        return new PluginLicenceEntity(
                dto.id(),
                dto.createdAt(),
                dto.duration(),
                new PluginEntity(dto.pluginId()),
                new UserEntity(dto.ownerId())
        );
    }
}
