package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.database.entity.PluginLicenceEntity;
import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.database.repository.PluginLicenceRepository;
import com.github.heroslender.hero_api.dto.NewLicenceDTO;
import com.github.heroslender.hero_api.dto.UpdateLicenceDTO;
import com.github.heroslender.hero_api.exceptions.ForbiddenException;
import com.github.heroslender.hero_api.exceptions.ResourceNotFoundException;
import com.github.heroslender.hero_api.exceptions.UnauthorizedException;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginLicence;
import com.github.heroslender.hero_api.model.PluginLicenceDtoMapper;
import com.github.heroslender.hero_api.model.PluginVisibility;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

@Service
public class PluginLicenceService {
    private final PluginLicenceRepository repository;
    private final Clock clock;

    public PluginLicenceService(PluginLicenceRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public Optional<PluginLicence> getLicenceOpt(UUID id) {
        return repository.findById(id).map(PluginLicenceDtoMapper::toDto);
    }

    public PluginLicence getLicence(UUID id) {
        return getLicenceOpt(id).orElseThrow(() -> new ResourceNotFoundException("Licence is not valid!"));
    }

    public PluginLicence validateLicence(UUID id) {
        PluginLicence licence = getLicence(id);

        if (licence.createdAt() + licence.duration() < clock.millis()) {
            throw new UnauthorizedException("Your licence has expired!");
        }

        return licence;
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

    public PluginLicence createLicence(UserEntity user, String pluginId, NewLicenceDTO request) {
        return null;
    }

    public PluginLicence updateLicence(UUID licence, UpdateLicenceDTO request) {
        return null;
    }

    public void deleteLicence(UUID licence) {

    }

    public UUID uuidFromString(String uuidString) {
        return UUID.fromString(uuidString);
    }
}
