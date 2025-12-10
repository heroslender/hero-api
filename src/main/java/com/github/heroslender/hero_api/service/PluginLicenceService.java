package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.database.entity.PluginEntity;
import com.github.heroslender.hero_api.database.entity.PluginLicenceEntity;
import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.database.repository.PluginLicenceRepository;
import com.github.heroslender.hero_api.dto.request.CreateLicenceRequest;
import com.github.heroslender.hero_api.dto.request.UpdateLicenceRequest;
import com.github.heroslender.hero_api.exceptions.ForbiddenException;
import com.github.heroslender.hero_api.exceptions.ResourceNotFoundException;
import com.github.heroslender.hero_api.exceptions.UnauthorizedException;
import com.github.heroslender.hero_api.model.*;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

@Service
public class PluginLicenceService {
    private final PluginLicenceRepository repository;
    private final PluginService pluginService;
    private final UserService userService;
    private final Clock clock;

    public PluginLicenceService(
            PluginLicenceRepository repository,
            PluginService pluginService,
            UserService userService,
            Clock clock
    ) {
        this.repository = repository;
        this.pluginService = pluginService;
        this.userService = userService;
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

    public PluginLicence createLicence(UserEntity user, String pluginId, CreateLicenceRequest request) {
        Plugin plugin = pluginService.getPlugin(pluginId);
        if (plugin.ownerId() != user.getId() && !user.hasRole(UserRole.ADMIN)) {
            throw new ForbiddenException("You are not the owner of this plugin.");
        }

        PluginLicenceEntity licence = new PluginLicenceEntity(
                null, clock.millis(), request.duration(), new PluginEntity(pluginId), user
        );

        return PluginLicenceDtoMapper.toDto(repository.save(licence));
    }

    public PluginLicence updateLicence(UUID licenceId, UpdateLicenceRequest request) {
        PluginLicenceEntity licence = repository.findById(licenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Licence is not valid!"));

        boolean hasUpdated = false;
        if (request.ownerId() != null) {
            UserEntity user = userService.getUser(request.ownerId());
            licence.setOwner(user);
            hasUpdated = true;
        }
        if (request.duration() != null) {
            licence.setDuration(request.duration());
            hasUpdated = true;
        }

        if (hasUpdated) {
            repository.save(licence);
        }

        return PluginLicenceDtoMapper.toDto(licence);
    }

    public void deleteLicence(UUID licence) {
        repository.deleteById(licence);
    }

    public UUID uuidFromString(String uuidString) {
        return UUID.fromString(uuidString);
    }
}
