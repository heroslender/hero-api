package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.dto.NewPluginVersionDto;
import com.github.heroslender.hero_api.dto.PluginDTO;
import com.github.heroslender.hero_api.dto.PluginVersionDTO;
import com.github.heroslender.hero_api.hateoas.PluginVersionDTOAssembler;
import com.github.heroslender.hero_api.service.PluginService;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plugins/{pluginId}/versions")
public class PluginVersionController {
    private final PluginService service;
    private final PluginVersionDTOAssembler pluginVersionAssembler;

    public PluginVersionController(PluginService service, PluginVersionDTOAssembler pluginVersionAssembler) {
        this.service = service;
        this.pluginVersionAssembler = pluginVersionAssembler;
    }

    @GetMapping
    public String versions(@PathVariable String pluginId) {
        return "Versions";
    }

    @GetMapping("/{version}")
    public String version(@PathVariable String pluginId, @PathVariable String version) {
        return pluginId + ": " + version;
    }

    @PostMapping("/{version}")
    public ResponseEntity<Object> addVersion(@PathVariable Long pluginId, @PathVariable String version, @RequestBody NewPluginVersionDto newVersion) {
        PluginDTO plugin = service.getPlugin(pluginId)
                .orElseThrow(() -> new PluginNotFoundException(pluginId));

        for (PluginVersionDTO ver : service.getVersions(plugin)) {
            if (ver.tag().equals(version)) {
                throw new RuntimeException("Plugin already has that version");
            }
        }

        PluginVersionDTO pluginVersion = new PluginVersionDTO(
                version,
                System.currentTimeMillis(),
                newVersion.releaseTitle(),
                newVersion.releaseNotes(),
                newVersion.downloadUrl(),
                0
        );


        PluginVersionDTO saved = service.addVersion(plugin, pluginVersion);

        EntityModel<PluginVersionDTO> entityModel = pluginVersionAssembler.toModel(saved);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }
}
