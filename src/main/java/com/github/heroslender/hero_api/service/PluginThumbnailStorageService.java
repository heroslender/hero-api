package com.github.heroslender.hero_api.service;

import com.github.heroslender.hero_api.config.StorageProperties;
import com.github.heroslender.hero_api.service.impl.FileSystemResourceStorageService;
import org.springframework.stereotype.Service;

@Service
public class PluginThumbnailStorageService extends FileSystemResourceStorageService {

    public PluginThumbnailStorageService(StorageProperties properties) {
        super(properties.getThumbnailsLocation());
    }
}
