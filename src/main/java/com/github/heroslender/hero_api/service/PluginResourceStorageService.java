package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.config.StorageProperties;
import com.github.heroslender.hero_api.exceptions.ResourceNotFoundException;
import com.github.heroslender.hero_api.exceptions.StorageException;
import com.github.heroslender.hero_api.service.impl.FileSystemResourceStorageService;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

@Service
public class PluginResourceStorageService extends FileSystemResourceStorageService {
    private final Path thumbnailsLocation;
    private final Path versionsLocation;

    private final Logger log = LoggerFactory.getLogger(PluginResourceStorageService.class.getName());

    public PluginResourceStorageService(StorageProperties properties) {
        String location = properties.getThumbnailsLocation();
        if (location.trim().isEmpty()) {
            log.error("Thumbnails location is empty!", new StorageException("Thumbnails location can not be Empty."));
            location = "thumbnails";
        }
        thumbnailsLocation = Path.of(location);

        location = properties.getVersionsLocation();
        if (location.trim().isEmpty()) {
            log.error("Versions location is empty!", new StorageException("Versions location can not be Empty."));
            location = "versions";
        }
        versionsLocation = Path.of(location);

        init(thumbnailsLocation, versionsLocation);
    }

    private String getThumbnailFileName(@NonNull String pluginId) {
        return pluginId + ".jpg";
    }

    public byte[] getThumbnail(@NonNull String pluginId) {
        try {
            return Files.readAllBytes(load(thumbnailsLocation, getThumbnailFileName(pluginId)));
        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException("Plugin thumbnail not found.");
        } catch (IOException e) {
            log.error("Failed to retrieve the plugin thumbnail for plugin '{}'.", pluginId, e);
            throw new StorageException("Failed to retrieve the plugin thumbnail.");
        }
    }

    public void storeThumbnail(@NonNull String pluginId, @NonNull MultipartFile file) {
        store(thumbnailsLocation, getThumbnailFileName(pluginId), file);
    }

    private String getVersionFileName(@NonNull String pluginId, @NonNull String versionTag) {
        return pluginId.toLowerCase(Locale.ROOT) + "-" + versionTag + ".jar";
    }

    public Resource getVersion(@NonNull String pluginId, @NonNull String versionTag) {
        return loadAsResource(versionsLocation, getVersionFileName(pluginId, versionTag));
    }

    public void storeVersion(@NonNull String pluginId, @NonNull String versionTag, @NonNull MultipartFile file) {
        store(versionsLocation, getVersionFileName(pluginId, versionTag), file);
    }
}
