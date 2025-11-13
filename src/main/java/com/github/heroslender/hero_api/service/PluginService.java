package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.exceptions.PluginNotFoundException;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginDtoMapper;
import com.github.heroslender.hero_api.model.PluginVersion;
import com.github.heroslender.hero_api.database.entity.PluginEntity;
import com.github.heroslender.hero_api.database.entity.PluginVersionEntity;
import com.github.heroslender.hero_api.exceptions.PluginVersionNotFoundException;
import com.github.heroslender.hero_api.database.repository.PluginRepository;
import com.github.heroslender.hero_api.database.repository.PluginVersionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PluginService {
    private final PluginRepository pluginRepository;
    private final PluginVersionRepository pluginVersionRepository;

    public PluginService(PluginRepository pluginRepository, PluginVersionRepository pluginVersionRepository) {
        this.pluginRepository = pluginRepository;
        this.pluginVersionRepository = pluginVersionRepository;
    }

    public List<Plugin> getPlugins() {
        return pluginRepository.findAll().stream().map(PluginDtoMapper::toDto).toList();
    }

    public Optional<Plugin> getPlugin(String id) {
        return pluginRepository.findByName(id).map(PluginDtoMapper::toDto);
    }

    public Plugin save(Plugin plugin, UserEntity owner) {
        PluginEntity entity = PluginDtoMapper.fromDto(plugin);
        entity.setOwner(owner);
        PluginEntity pl = pluginRepository.save(entity);

        return PluginDtoMapper.toDto(pl);
    }

    public PluginVersion addVersion(String pluginId, PluginVersion pluginVersion) {
        Plugin plugin = getPlugin(pluginId).orElseThrow(() -> new PluginNotFoundException(pluginId));

        PluginVersionEntity saved = pluginVersionRepository.save(PluginDtoMapper.fromDto(pluginVersion, plugin));
        return PluginDtoMapper.toDto(saved);
    }

    public void delete(String id) {
        pluginRepository.deleteByName(id);
    }

    public List<PluginVersion> getVersions(String pluginId) {
        PluginEntity pl = pluginRepository.findByName(pluginId).orElseThrow(() -> new PluginNotFoundException(pluginId));

        return pl.getVersions().stream().map(PluginDtoMapper::toDto).toList();
    }

    public PluginVersion getVersion(String pluginId, String versionTag) {
        List<PluginVersion> versions = getVersions(pluginId);

        for (PluginVersion version : versions) {
            if (version.tag().equals(versionTag)) {
                return version;
            }
        }

        throw new PluginVersionNotFoundException(versionTag);
    }

    public void deleteVersion(long version) {
        pluginVersionRepository.deleteById(version);
    }
}
