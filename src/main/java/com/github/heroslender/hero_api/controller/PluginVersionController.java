package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.dto.NewPluginVersionDto;
import com.github.heroslender.hero_api.dto.PluginVersionDTO;
import com.github.heroslender.hero_api.exceptions.DuplicatePluginVersionException;
import com.github.heroslender.hero_api.exceptions.PluginVersionNotFoundException;
import com.github.heroslender.hero_api.hateoas.PluginVersionAssembler;
import com.github.heroslender.hero_api.security.RequireAdmin;
import com.github.heroslender.hero_api.service.PluginService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/plugins/{pluginId}/versions")
public class PluginVersionController {
    private final PluginService service;
    private final PluginVersionAssembler pluginVersionAssembler;

    public PluginVersionController(PluginService service, PluginVersionAssembler pluginVersionAssembler) {
        this.service = service;
        this.pluginVersionAssembler = pluginVersionAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<PluginVersionDTO>> versions(@PathVariable long pluginId) {
        List<PluginVersionDTO> versions = service.getVersions(pluginId);

        return pluginVersionAssembler.toCollectionModel(versions);
    }

    @GetMapping("/{version}")
    public EntityModel<PluginVersionDTO> version(@PathVariable long pluginId, @PathVariable String version) {
        PluginVersionDTO ver = service.getVersion(pluginId, version);

        return pluginVersionAssembler.toModel(ver);
    }

    @PostMapping("/{version}")
    @RequireAdmin
    public ResponseEntity<Object> addVersion(@PathVariable Long pluginId, @PathVariable String version, @RequestBody NewPluginVersionDto newVersion) {
        try {
            service.getVersion(pluginId, version);
            throw new DuplicatePluginVersionException(version);
        } catch (PluginVersionNotFoundException ignored) { // Version does not exist
        }

        PluginVersionDTO pluginVersion = new PluginVersionDTO(
                pluginId,
                version,
                System.currentTimeMillis(),
                newVersion.releaseTitle(),
                newVersion.releaseNotes(),
                newVersion.downloadUrl(),
                0
        );

        PluginVersionDTO saved = service.addVersion(pluginId, pluginVersion);
        EntityModel<PluginVersionDTO> entityModel = pluginVersionAssembler.toModel(saved);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{version}")
    @RequireAdmin
    public ResponseEntity<Void> delete(@PathVariable Long version) {
        service.deleteVersion(version);

        return ResponseEntity.noContent().build();
    }
}
