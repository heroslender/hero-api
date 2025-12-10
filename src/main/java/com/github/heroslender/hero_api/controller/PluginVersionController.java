package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.controller.hateoas.PluginVersionAssembler;
import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.dto.NewPluginVersionDto;
import com.github.heroslender.hero_api.exceptions.DuplicatePluginVersionException;
import com.github.heroslender.hero_api.exceptions.ForbiddenException;
import com.github.heroslender.hero_api.exceptions.PluginVersionNotFoundException;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginVersion;
import com.github.heroslender.hero_api.model.UserRole;
import com.github.heroslender.hero_api.security.RequireDeveloperRole;
import com.github.heroslender.hero_api.service.PluginLicenceService;
import com.github.heroslender.hero_api.service.PluginService;
import com.github.heroslender.hero_api.service.PluginVersionStorageService;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/plugins/{pluginId}/versions")
public class PluginVersionController {
    private final PluginService service;
    private final PluginVersionStorageService storageService;
    private final PluginLicenceService licenceService;
    private final PluginVersionAssembler pluginVersionAssembler;

    public PluginVersionController(
            PluginService service,
            PluginVersionStorageService storageService, PluginLicenceService licenceService,
            PluginVersionAssembler pluginVersionAssembler
    ) {
        this.service = service;
        this.storageService = storageService;
        this.licenceService = licenceService;
        this.pluginVersionAssembler = pluginVersionAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<PluginVersion>> versions(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable String pluginId
    ) {
        Plugin plugin = service.getPlugin(pluginId);
        licenceService.checkUserAccessToPlugin(user, plugin);

        List<PluginVersion> versions = service.getVersions(pluginId);

        return pluginVersionAssembler.toCollectionModel(versions);
    }

    @GetMapping("/{version}")
    public EntityModel<PluginVersion> version(@PathVariable String pluginId, @PathVariable String version) {
        PluginVersion ver = service.getVersion(pluginId, version);

        return pluginVersionAssembler.toModel(ver);
    }

    @PostMapping("/{version}")
    @RequireDeveloperRole
    public ResponseEntity<EntityModel<PluginVersion>> addVersion(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable String pluginId,
            @PathVariable String version,
            @RequestBody NewPluginVersionDto newVersion
    ) {
        Plugin plugin = service.getPlugin(pluginId);
        if (plugin.ownerId() != user.getId() && !user.hasRole(UserRole.ADMIN)) {
            throw new ForbiddenException("You are not the owner of this plugin.");
        }

        try {
            service.getVersion(pluginId, version);
            throw new DuplicatePluginVersionException(version);
        } catch (PluginVersionNotFoundException ignored) { // Version does not exist
        }

        PluginVersion saved = service.addVersion(pluginId, version, newVersion);
        EntityModel<PluginVersion> entityModel = pluginVersionAssembler.toModel(saved);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{version}")
    @RequireDeveloperRole
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable String pluginId,
            @PathVariable String version
    ) {
        Plugin plugin = service.getPlugin(pluginId);
        if (plugin.ownerId() != user.getId() && !user.hasRole(UserRole.ADMIN)) {
            throw new ForbiddenException("You are not the owner of this plugin.");
        }

        PluginVersion ver = service.getVersion(pluginId, version);
        service.deleteVersion(ver.id());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{version}/download")
    public ResponseEntity<Resource> download(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable String pluginId,
            @PathVariable String version
    ) {
        Plugin plugin = service.getPlugin(pluginId);
        licenceService.checkUserAccessToPlugin(user, plugin);

        String filename = buildFilename(pluginId, version);
        Resource file = storageService.loadAsResource(filename);

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/{version}/upload")
    @RequireDeveloperRole
    public ResponseEntity<Void> upload(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable String pluginId,
            @PathVariable String version,
            @RequestParam("file") MultipartFile file
    ) {
        Plugin plugin = service.getPlugin(pluginId);
        if (plugin.ownerId() != user.getId() && !user.hasRole(UserRole.ADMIN)) {
            throw new ForbiddenException("You are not the owner of this plugin.");
        }

        String filename = buildFilename(pluginId, version);
        storageService.store(filename, file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    private String buildFilename(String pluginId, String versionTag) {
        return pluginId.toLowerCase(Locale.ROOT) + "-" + versionTag + ".jar";
    }
}
