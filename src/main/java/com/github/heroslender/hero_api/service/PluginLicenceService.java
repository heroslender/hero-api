package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.database.entity.PluginLicenceEntity;
import com.github.heroslender.hero_api.database.repository.PluginLicenceRepository;
import org.springframework.stereotype.Service;

@Service
public class PluginLicenceService {
    private final PluginLicenceRepository repository;

    public PluginLicenceService(PluginLicenceRepository repository) {
        this.repository = repository;
    }

    public boolean hasLicence(Long userId, String plugin) {
        for (PluginLicenceEntity licence : repository.findByOwnerId(userId)) {
            if (licence.getPlugin().getName().equalsIgnoreCase(plugin)) {
                return true;
            }
        }

        return false;
    }
}
