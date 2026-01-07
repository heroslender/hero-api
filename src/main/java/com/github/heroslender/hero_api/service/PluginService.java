package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.database.entity.PluginEntity;
import com.github.heroslender.hero_api.database.entity.PluginVersionEntity;
import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.database.repository.PluginRepository;
import com.github.heroslender.hero_api.database.repository.PluginVersionRepository;
import com.github.heroslender.hero_api.dto.request.CreatePluginRequest;
import com.github.heroslender.hero_api.dto.request.CreatePluginVersionRequest;
import com.github.heroslender.hero_api.dto.request.UpdatePluginRequest;
import com.github.heroslender.hero_api.exceptions.PluginNotFoundException;
import com.github.heroslender.hero_api.exceptions.PluginVersionNotFoundException;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginVersion;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

@Service
public class PluginService {
    private final PluginService self;
    private final PluginRepository pluginRepository;
    private final PluginVersionRepository pluginVersionRepository;
    private final Clock clock;
    private final EntityManager entityManager;

    private final Logger log = LoggerFactory.getLogger(PluginService.class);

    public PluginService(PluginRepository pluginRepository, PluginVersionRepository pluginVersionRepository, Clock clock, EntityManager entityManager) {
        this.self = this;
        this.pluginRepository = pluginRepository;
        this.pluginVersionRepository = pluginVersionRepository;
        this.clock = clock;
        this.entityManager = entityManager;
    }

    /**
     * Get all plugins saved in the database.
     *
     * @return A list containing all stored plugins
     */
    @Cacheable("plugin-list")
    public List<Plugin> getPlugins() {
        log.info("Getting all plugins");
        return pluginRepository.findAll().stream().map(this::toDto).toList();
    }

    /**
     * Get a plugin by its ID.
     *
     * @param id The ID of the plugin
     * @return The requested plugin
     * @throws PluginNotFoundException If the plugin was not found
     */
    @Cacheable(value = "plugins", key = "#id")
    public Plugin getPlugin(String id) {
        log.info("Getting plugin with ID '{}'", id);
        return getPluginOpt(id).orElseThrow(() -> new PluginNotFoundException(id));
    }

    /**
     * Get a plugin by its ID.
     *
     * @param id The ID of the plugin
     * @return An Optional containing the plugin if found
     */
    public Optional<Plugin> getPluginOpt(String id) {
        return pluginRepository.findById(id).map(this::toDto);
    }

    /**
     * Save a plugin to the database.
     *
     * @param request The plugin details to save
     * @param owner   The owner of the plugin
     * @return The saved plugin
     */
    @CacheEvict(value = "plugin-list", allEntries = true)
    @CachePut(value = "plugins", key = "#request.id")
    public Plugin newPlugin(CreatePluginRequest request, UserEntity owner) {
        log.info("Saving plugin with ID '{}'", request.id());
        PluginEntity entity = new PluginEntity();
        entity.setId(request.id());
        entity.setName(request.name());
        entity.setOwner(owner);
        entity.setVisibility(request.visibility());
        entity.setPrice(request.price());
        entity.setPromoPrice(null);
        entity.setTagline(request.tagline());
        entity.setDescription(request.description());

        PluginEntity pl = pluginRepository.save(entity);

        return toDto(pl);
    }

    /**
     * Update some plugin.
     *
     * @param plugin  The plugin to be updated
     * @param request The new data
     * @return The updated plugin
     */
    @CacheEvict(value = "plugin-list", allEntries = true)
    @CachePut(value = "plugins", key = "#plugin.id")
    public Plugin update(Plugin plugin, UpdatePluginRequest request) {
        log.info("Updating plugin with ID '{}'", plugin.id());
        PluginEntity entity = fromDto(plugin);

        if (request.name() != null) {
            entity.setName(request.name());
        }
        if (request.visibility() != null) {
            entity.setVisibility(request.visibility());
        }
        if (request.price() != null) {
            entity.setPrice(request.price());
        }
        if (request.promoPrice() != null) {
            entity.setPromoPrice(request.promoPrice());
        }
        if (request.tagline() != null) {
            entity.setTagline(request.tagline());
        }
        if (request.description() != null) {
            entity.setDescription(request.description());
        }

        PluginEntity pl = pluginRepository.save(entity);
        return toDto(pl);
    }

    /**
     * Add a version to a plugin.
     *
     * @param pluginId The plugin that will get the new version
     * @param tag      The version tag
     * @param request  The version data
     * @return The added plugin version
     * @throws PluginNotFoundException If the plugin was not found
     */
    public PluginVersion addVersion(String pluginId, String tag, CreatePluginVersionRequest request) {
        self.getPlugin(pluginId);

        PluginVersion pluginVersion = new PluginVersion(
                pluginId,
                tag,
                clock.millis(),
                request.releaseTitle(),
                request.releaseNotes(),
                0
        );

        PluginVersionEntity saved = pluginVersionRepository.save(fromDto(pluginVersion));
        return toDto(saved);
    }

    /**
     * Delete a plugin from the database.
     *
     * @param id The ID of the plugin
     */
    @Caching(evict = {
            @CacheEvict(value = "plugin-list", allEntries = true),
            @CacheEvict(value = "plugins", key = "#id")
    })
    public void delete(String id) {
        log.info("Deleting plugin with ID '{}'", id);
        pluginRepository.deleteById(id);
    }

    /**
     * Get all versions for a plugin.
     *
     * @param pluginId The plugin to get the versions from
     * @return A list containing all versions
     * @throws PluginNotFoundException If the plugin was not found
     */
    public List<PluginVersion> getVersions(String pluginId) {
        PluginEntity pl = pluginRepository.findById(pluginId).orElseThrow(() -> new PluginNotFoundException(pluginId));

        return pl.getVersions().stream().map(this::toDto).toList();
    }

    /**
     * Get a specific version for a plugin
     *
     * @param pluginId   The ID of the plugin
     * @param versionTag The desired version tag
     * @return The requested version
     * @throws PluginNotFoundException        If the plugin was not found
     * @throws PluginVersionNotFoundException If the requested version was not found
     */
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


    public Plugin toDto(PluginEntity plugin) {
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

    public PluginEntity fromDto(Plugin dto) {
        return new PluginEntity(
                dto.id(),
                dto.name(),
                entityManager.getReference(UserEntity.class, dto.ownerId()),
                dto.visibility(),
                dto.price(),
                dto.promoPrice(),
                dto.tagline(),
                dto.description(),
                null
        );
    }

    public PluginVersion toDto(PluginVersionEntity version) {
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

    public PluginVersionEntity fromDto(PluginVersion dto) {
        return new PluginVersionEntity(
                dto.id(),
                entityManager.getReference(PluginEntity.class, dto.pluginId()),
                dto.tag(),
                dto.releasedAt(),
                dto.releaseTitle(),
                dto.releaseNotes(),
                dto.downloadCount()
        );
    }
}
