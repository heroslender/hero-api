package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.controller.hateoas.PluginAssembler;
import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.dto.request.CreatePluginRequest;
import com.github.heroslender.hero_api.exceptions.ForbiddenException;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginVisibility;
import com.github.heroslender.hero_api.model.UserRole;
import com.github.heroslender.hero_api.security.RequireAdminRole;
import com.github.heroslender.hero_api.security.RequireDeveloperRole;
import com.github.heroslender.hero_api.service.PluginLicenceService;
import com.github.heroslender.hero_api.service.PluginService;
import com.github.heroslender.hero_api.service.PluginThumbnailStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PluginController {
    private final PluginService service;
    private final PluginLicenceService licenceService;
    private final PluginThumbnailStorageService thumbnailService;
    private final PluginAssembler assembler;

    @GetMapping("/plugins")
    public CollectionModel<EntityModel<Plugin>> plugins(@AuthenticationPrincipal UserEntity user) {
        List<Plugin> availablePlugins = new ArrayList<>();
        for (Plugin plugin : service.getPlugins()) {
            if (licenceService.userHasAccessToPlugin(user, plugin)) {
                availablePlugins.add(plugin);
            }
        }

        return assembler.toCollectionModel(availablePlugins);
    }


    @PostMapping("/plugins")
    @RequireDeveloperRole
    public ResponseEntity<EntityModel<Plugin>> newPlugin(
            @AuthenticationPrincipal UserEntity user,
            @RequestBody CreatePluginRequest request
    ) {
        Plugin plugin = new Plugin(
                request.name(),
                user.getId(),
                PluginVisibility.PUBLIC,
                request.displayName(),
                request.description()
        );
        EntityModel<Plugin> entityModel = assembler.toModel(service.save(plugin, user));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/plugins/{id}")
    public EntityModel<Plugin> plugin(@AuthenticationPrincipal UserEntity user, @PathVariable String id) {
        Plugin plugin = service.getPlugin(id);
        licenceService.checkUserAccessToPlugin(user, plugin);

        return assembler.toModel(plugin);
    }

    @GetMapping("/plugins/{id}/thumbnail")
    public ResponseEntity<byte[]> getPluginThumbnail(@PathVariable String id) throws IOException {
        service.getPlugin(id);
        byte[] bytes = Files.readAllBytes(thumbnailService.load(id + ".jpg"));
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
    }

    @PostMapping("/plugins/{id}/thumbnail")
    @RequireDeveloperRole
    public ResponseEntity<Void> upload(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable String id,
            @RequestParam("file") MultipartFile file
    ) {
        Plugin plugin = service.getPlugin(id);
        if (plugin.ownerId() != user.getId() && !user.hasRole(UserRole.ADMIN)) {
            throw new ForbiddenException("You are not the owner of this plugin.");
        }

        thumbnailService.store(id + ".jpg", file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping("/plugins/{id}")
    @RequireAdminRole
    public ResponseEntity<EntityModel<Plugin>> updatePlugin(
            @AuthenticationPrincipal UserEntity user,
            @RequestBody Plugin newPlugin,
            @PathVariable String id
    ) {
        Plugin updatedPlugin = service.save(newPlugin, user);

        EntityModel<Plugin> entityModel = assembler.toModel(updatedPlugin);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/plugins/{id}")
    @RequireAdminRole
    public ResponseEntity<Void> deletePlugin(@PathVariable String id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
