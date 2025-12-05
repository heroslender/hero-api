package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.database.entity.PluginLicenceEntity;
import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.database.repository.PluginLicenceRepository;
import com.github.heroslender.hero_api.exceptions.ForbiddenException;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginVisibility;
import org.springframework.stereotype.Service;

@Service
public class PluginLicenceService {
    private final PluginLicenceRepository repository;

    public PluginLicenceService(PluginLicenceRepository repository) {
        this.repository = repository;
    }

    public boolean userHasAccessToPlugin(UserEntity user, Plugin plugin) {
        return plugin.visibility() == PluginVisibility.PUBLIC
                || (plugin.visibility() == PluginVisibility.REQUIRE_LICENCE
                && user != null
                && hasLicence(user.getId(), plugin.name()));
    }

    public void checkUserAccessToPlugin(UserEntity user, Plugin plugin) {
        if (!userHasAccessToPlugin(user, plugin)) throw new ForbiddenException("You don't have access to this!");
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
