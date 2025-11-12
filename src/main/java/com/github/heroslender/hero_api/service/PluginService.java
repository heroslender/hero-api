package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.exceptions.PluginNotFoundException;
import com.github.heroslender.hero_api.dto.PluginDTO;
import com.github.heroslender.hero_api.dto.PluginDtoMapper;
import com.github.heroslender.hero_api.dto.PluginVersionDTO;
import com.github.heroslender.hero_api.entity.Plugin;
import com.github.heroslender.hero_api.entity.PluginVersion;
import com.github.heroslender.hero_api.exceptions.PluginVersionNotFoundException;
import com.github.heroslender.hero_api.repository.PluginRepository;
import com.github.heroslender.hero_api.repository.PluginVersionRepository;
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

    public List<PluginDTO> getPlugins() {
        return pluginRepository.findAll().stream().map(PluginDtoMapper::toDto).toList();
    }

    public Optional<PluginDTO> getPlugin(long id) {
        return pluginRepository.findById(id).map(PluginDtoMapper::toDto);
    }

    public PluginDTO save(PluginDTO plugin) {
        Plugin pl = pluginRepository.save(PluginDtoMapper.fromDto(plugin));

        return PluginDtoMapper.toDto(pl);
    }

    public PluginVersionDTO addVersion(long pluginId, PluginVersionDTO pluginVersion) {
        PluginDTO plugin = getPlugin(pluginId).orElseThrow(() -> new PluginNotFoundException(pluginId));

        PluginVersion saved = pluginVersionRepository.save(PluginDtoMapper.fromDto(pluginVersion, plugin));
        return PluginDtoMapper.toDto(saved);
    }

    public void delete(Long id) {
        pluginRepository.deleteById(id);
    }

    public List<PluginVersionDTO> getVersions(long pluginId) {
        Plugin pl = pluginRepository.findById(pluginId).orElseThrow(() -> new PluginNotFoundException(pluginId));

        return pl.getVersions().stream().map(PluginDtoMapper::toDto).toList();
    }

    public PluginVersionDTO getVersion(long pluginId, String versionTag) {
        List<PluginVersionDTO> versions = getVersions(pluginId);

        for (PluginVersionDTO version : versions) {
            if (version.tag().equals(versionTag)) {
                return version;
            }
        }

        throw new PluginVersionNotFoundException(versionTag);
    }

    public void deleteVersion(Long version) {
        pluginVersionRepository.deleteById(version);
    }
}
