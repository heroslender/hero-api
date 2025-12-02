package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.controller.hateoas.PluginAssembler;
import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.dto.NewPluginDto;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginVisibility;
import com.github.heroslender.hero_api.security.RequireAdminRole;
import com.github.heroslender.hero_api.service.PluginService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class PluginController {
    private final PluginService service;
    private final PluginAssembler assembler;

    public PluginController(PluginService service, PluginAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping("/plugins")
    public CollectionModel<EntityModel<Plugin>> plugins() {
        return assembler.toCollectionModel(service.getPlugins());
    }


    @PostMapping("/plugins")
    @RequireAdminRole
    public ResponseEntity<EntityModel<Plugin>> newPlugin(
            @AuthenticationPrincipal UserEntity user,
            @RequestBody NewPluginDto newPlugin
    ) {
        Plugin plugin = new Plugin(
                newPlugin.name(),
                user.getId(),
                PluginVisibility.PUBLIC,
                newPlugin.displayName(),
                newPlugin.description()
        );
        EntityModel<Plugin> entityModel = assembler.toModel(service.save(plugin, user));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/plugins/{id}")
    public EntityModel<Plugin> plugin(@PathVariable String id) {
        Plugin plugin = service.getPlugin(id);

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
