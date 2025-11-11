package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.dto.PluginDTO;
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
    public CollectionModel<EntityModel<PluginDTO>> all() {
        return assembler.toCollectionModel(service.getPlugins());
    }


    @PostMapping("/plugins")
    @RequireAdmin
    public ResponseEntity<EntityModel<PluginDTO>> newPlugin(@RequestBody PluginDTO newPlugin) {
        EntityModel<PluginDTO> entityModel = assembler.toModel(service.save(newPlugin));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/plugins/{id}")
    public EntityModel<PluginDTO> one(@PathVariable Long id) {
        PluginDTO plugin = service.getPlugin(id)
                .orElseThrow(() -> new PluginNotFoundException(id));

        return assembler.toModel(plugin);
    }

    @PutMapping("/plugins/{id}")
    @RequireAdmin
    public ResponseEntity<EntityModel<PluginDTO>> replacePlugin(@RequestBody PluginDTO newPlugin, @PathVariable Long id) {
        PluginDTO updatedPlugin = service.save(newPlugin);

        EntityModel<PluginDTO> entityModel = assembler.toModel(updatedPlugin);

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
