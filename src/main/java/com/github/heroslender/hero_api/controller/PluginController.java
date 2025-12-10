package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.controller.hateoas.PluginAssembler;
import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.dto.request.CreatePluginRequest;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginVisibility;
import com.github.heroslender.hero_api.security.RequireAdminRole;
import com.github.heroslender.hero_api.service.PluginLicenceService;
import com.github.heroslender.hero_api.service.PluginService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PluginController {
    private final PluginService service;
    private final PluginLicenceService licenceService;
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
    @RequireAdminRole
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

    @PutMapping("/plugins/{id}")
    @RequireAdminRole
    public ResponseEntity<EntityModel<Plugin>> replacePlugin(
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
