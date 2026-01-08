package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.database.entity.PluginEntity;
import com.github.heroslender.hero_api.database.entity.PluginVersionEntity;
import com.github.heroslender.hero_api.database.repository.PluginVersionRepository;
import com.github.heroslender.hero_api.dto.request.CreatePluginVersionRequest;
import com.github.heroslender.hero_api.exceptions.PluginNotFoundException;
import com.github.heroslender.hero_api.exceptions.PluginVersionNotFoundException;
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

@Service
public class PluginVersionService {
    private final PluginVersionRepository repository;
    private final PluginService pluginService;
    private final PluginVersionService self;

    private final EntityManager entityManager;
    private final Clock clock;

    private final Logger log;

    public PluginVersionService(PluginVersionRepository repository, PluginService pluginService, EntityManager entityManager, Clock clock) {
        this.repository = repository;
        this.pluginService = pluginService;
        this.entityManager = entityManager;
        this.clock = clock;

        self = this;
        log = LoggerFactory.getLogger(PluginVersionService.class);
    }

    /**
     * Get all versions for a plugin.
     *
     * @param pluginId The plugin to get the versions from
     * @return A list containing all versions
     * @throws PluginNotFoundException If the plugin was not found
     */
    @Cacheable(value = "plugin-versions-list", key = "{#pluginId}")
    public List<PluginVersion> getVersions(String pluginId) {
        log.info("Getting versions for plugin with ID '{}'", pluginId);
        pluginService.testPluginExists(pluginId);

        return repository.findByPluginId(pluginId).stream().map(this::toDto).toList();
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
    @Cacheable(value = "plugin-versions", key = "{#pluginId, #versionTag}")
    public PluginVersion getVersion(String pluginId, String versionTag) {
        log.info("Getting version '{}' for plugin with ID '{}'", versionTag, pluginId);
        pluginService.testPluginExists(pluginId);

        List<PluginVersion> versions = self.getVersions(pluginId);

        if (versionTag.equalsIgnoreCase("latest")) {
            PluginVersion latest = null;
            long latestTimestamp = -1;

            for (PluginVersion version : versions) {
                if (latest == null || version.releasedAt() > latestTimestamp) {
                    latest = version;
                    latestTimestamp = version.releasedAt();
                }
            }

            if (latest != null) {
                return latest;
            }
        } else {
            for (PluginVersion version : versions) {
                if (version.tag().equals(versionTag)) {
                    return version;
                }
            }
        }

        throw new PluginVersionNotFoundException(versionTag);
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
    @CacheEvict(value = "plugin-versions-list", allEntries = true)
    @CachePut(value = "plugin-versions", key = "{#pluginId, #tag}")
    public PluginVersion addVersion(String pluginId, String tag, CreatePluginVersionRequest request) {
        pluginService.testPluginExists(pluginId);

        PluginVersion pluginVersion = new PluginVersion(
                pluginId,
                tag,
                clock.millis(),
                request.releaseTitle(),
                request.releaseNotes(),
                0
        );

        PluginVersionEntity saved = repository.save(fromDto(pluginVersion));
        log.info("Added version '{}' for plugin with ID '{}'", tag, pluginId);

        return toDto(saved);
    }

    @Caching(evict = {
            @CacheEvict(value = "plugin-versions-list", allEntries = true),
            @CacheEvict(value = "plugin-versions", allEntries = true)
    })
    public void deleteVersion(long version) {
        repository.deleteById(version);
        log.info("Deleted version with ID '{}'", version);
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
