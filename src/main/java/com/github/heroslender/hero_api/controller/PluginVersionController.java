package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.controller.hateoas.PluginVersionAssembler;
import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.database.entity.UserRole;
import com.github.heroslender.hero_api.dto.NewPluginVersionDto;
import com.github.heroslender.hero_api.exceptions.*;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginVersion;
import com.github.heroslender.hero_api.security.RequireAdmin;
import com.github.heroslender.hero_api.security.RequireUser;
import com.github.heroslender.hero_api.service.impl.FileSystemPluginVersionStorageService;
import com.github.heroslender.hero_api.service.PluginService;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpHeaders;
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
    private final FileSystemPluginVersionStorageService storageService;
    private final PluginVersionAssembler pluginVersionAssembler;

    public PluginVersionController(
            PluginService service,
            FileSystemPluginVersionStorageService storageService,
            PluginVersionAssembler pluginVersionAssembler
    ) {
        this.service = service;
        this.storageService = storageService;
        this.pluginVersionAssembler = pluginVersionAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<PluginVersion>> versions(@PathVariable String pluginId) {
        List<PluginVersion> versions = service.getVersions(pluginId);

        return pluginVersionAssembler.toCollectionModel(versions);
    }

    @GetMapping("/{version}")
    public EntityModel<PluginVersion> version(@PathVariable String pluginId, @PathVariable String version) {
        PluginVersion ver = service.getVersion(pluginId, version);

        return pluginVersionAssembler.toModel(ver);
    }

    @PostMapping("/{version}")
    @RequireUser
    public ResponseEntity<EntityModel<PluginVersion>> addVersion(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable String pluginId,
            @PathVariable String version,
            @RequestBody NewPluginVersionDto newVersion
    ) {
        Plugin plugin = service.getPlugin(pluginId).orElseThrow(() -> new PluginNotFoundException(pluginId));
        if (plugin.ownerId() != user.getId() && !user.hasRole(UserRole.ADMIN)) {
            throw new ForbiddenException("You are not the owner of this plugin.");
        }

        try {
            service.getVersion(pluginId, version);
            throw new DuplicatePluginVersionException(version);
        } catch (PluginVersionNotFoundException ignored) { // Version does not exist
        }

        PluginVersion pluginVersion = new PluginVersion(
                pluginId,
                version,
                System.currentTimeMillis(),
                newVersion.releaseTitle(),
                newVersion.releaseNotes(),
                newVersion.downloadUrl(),
                0
        );

        PluginVersion saved = service.addVersion(pluginId, pluginVersion);
        EntityModel<PluginVersion> entityModel = pluginVersionAssembler.toModel(saved);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{version}")
    @RequireAdmin
    public ResponseEntity<Void> delete(@PathVariable String pluginId, @PathVariable String version) {
        PluginVersion ver = service.getVersion(pluginId, version);
        service.deleteVersion(ver.id());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{version}/download")
    public ResponseEntity<Resource> serveFile(@PathVariable String pluginId, @PathVariable String version) {
        String filename = buildFilename(pluginId, version);
        Resource file = storageService.loadAsResource(filename);

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/{version}/upload")
    public ResponseEntity<Void> handleFileUpload(
            @PathVariable String pluginId,
            @PathVariable String version,
            @RequestParam("file") MultipartFile file
    ) {
        String filename = buildFilename(pluginId, version);
        storageService.store(filename, file);

        return ResponseEntity.ok().build();
    }

    private String buildFilename(String pluginId, String versionTag) {
        return pluginId.toLowerCase(Locale.ROOT) + "-" + versionTag + ".jar";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
