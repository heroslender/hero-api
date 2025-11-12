package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.dto.NewPluginVersionDto;
import com.github.heroslender.hero_api.model.PluginVersion;
import com.github.heroslender.hero_api.exceptions.DuplicatePluginVersionException;
import com.github.heroslender.hero_api.exceptions.PluginVersionNotFoundException;
import com.github.heroslender.hero_api.controller.hateoas.PluginVersionAssembler;
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
    public CollectionModel<EntityModel<PluginVersion>> versions(@PathVariable long pluginId) {
        List<PluginVersion> versions = service.getVersions(pluginId);

        return pluginVersionAssembler.toCollectionModel(versions);
    }

    @GetMapping("/{version}")
    public EntityModel<PluginVersion> version(@PathVariable long pluginId, @PathVariable String version) {
        PluginVersion ver = service.getVersion(pluginId, version);

        return pluginVersionAssembler.toModel(ver);
    }

    @PostMapping("/{version}")
    @RequireAdmin
    public ResponseEntity<EntityModel<PluginVersion>> addVersion(@PathVariable Long pluginId, @PathVariable String version, @RequestBody NewPluginVersionDto newVersion) {
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
    public ResponseEntity<Void> delete(@PathVariable Long version) {
        service.deleteVersion(version);

        return ResponseEntity.noContent().build();
    }
}
