package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.entity.Plugin;
import com.github.heroslender.hero_api.repository.PluginRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PluginService {
    private final PluginRepository pluginRepository;

    public PluginService(PluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
    }

    public List<Plugin> getPlugins() {
        return pluginRepository.findAll();
    }

    public Optional<Plugin> getPlugin(long id) {
        return pluginRepository.findById(id);
    }

    public Plugin save(Plugin plugin) {
        return pluginRepository.save(plugin);
    }

    public void delete(Long id) {
        pluginRepository.deleteById(id);
    }
}
