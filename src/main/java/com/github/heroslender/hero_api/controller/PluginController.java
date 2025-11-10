package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.entity.Plugin;
import com.github.heroslender.hero_api.hateoas.PluginAssembler;
import com.github.heroslender.hero_api.security.RequireAdmin;
import com.github.heroslender.hero_api.service.PluginService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
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
    public CollectionModel<EntityModel<Plugin>> all() {
        return assembler.toCollectionModel(service.getPlugins());
    }


    @PostMapping("/plugins")
    @RequireAdmin
    public ResponseEntity<EntityModel<Plugin>> newPlugin(@RequestBody Plugin newPlugin) {
        EntityModel<Plugin> entityModel = assembler.toModel(service.save(newPlugin));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/plugins/{id}")
    public EntityModel<Plugin> one(@PathVariable Long id) {
        Plugin plugin = service.getPlugin(id)
                .orElseThrow(() -> new PluginNotFoundException(id));

        return assembler.toModel(plugin);
    }

    @PutMapping("/plugins/{id}")
    @RequireAdmin
    public ResponseEntity<EntityModel<Plugin>> replacePlugin(@RequestBody Plugin newPlugin, @PathVariable Long id) {
        Plugin updatedPlugin = service.getPlugin(id)
                .map(employee -> {
                    employee.setName(newPlugin.getName());
                    return service.save(employee);
                })
                .orElseGet(() -> service.save(newPlugin));

        EntityModel<Plugin> entityModel = assembler.toModel(updatedPlugin);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/plugins/{id}")
    @RequireAdmin
    public ResponseEntity<Object> deletePlugin(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}
