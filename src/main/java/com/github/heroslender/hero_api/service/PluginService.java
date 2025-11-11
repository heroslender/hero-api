package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.dto.PluginDTO;
import com.github.heroslender.hero_api.dto.PluginDtoMapper;
import com.github.heroslender.hero_api.dto.PluginVersionDTO;
import com.github.heroslender.hero_api.entity.Plugin;
import com.github.heroslender.hero_api.entity.PluginVersion;
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

    public PluginVersionDTO addVersion(PluginDTO plugin, PluginVersionDTO pluginVersion) {
        PluginVersion saved = pluginVersionRepository.save(PluginDtoMapper.fromDto(pluginVersion, plugin));
        return PluginDtoMapper.toDto(saved);
    }

    public void delete(Long id) {
        pluginRepository.deleteById(id);
    }

    public List<PluginVersionDTO> getVersions(PluginDTO plugin) {
        return pluginRepository.findById(plugin.id())
                .map(Plugin::getVersions)
                .map(ver -> ver.stream().map(PluginDtoMapper::toDto).toList())
                .get();
    }
}
