package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.controller.hateoas.PluginLicenceAssembler;
import com.github.heroslender.hero_api.database.entity.UserEntity;
import com.github.heroslender.hero_api.dto.NewLicenceDTO;
import com.github.heroslender.hero_api.dto.UpdateLicenceDTO;
import com.github.heroslender.hero_api.exceptions.ForbiddenException;
import com.github.heroslender.hero_api.model.Plugin;
import com.github.heroslender.hero_api.model.PluginLicence;
import com.github.heroslender.hero_api.model.UserRole;
import com.github.heroslender.hero_api.service.PluginLicenceService;
import com.github.heroslender.hero_api.service.PluginService;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plugins/{pluginId}/licence")
public class PluginLicenceController {
    private final PluginLicenceService service;
    private final PluginService pluginService;
    private final PluginLicenceAssembler assembler;

    public PluginLicenceController(PluginLicenceService service, PluginService pluginService, PluginLicenceAssembler assembler) {
        this.service = service;
        this.pluginService = pluginService;
        this.assembler = assembler;
    }

    @PostMapping()
    public EntityModel<PluginLicence> createLicence(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable String pluginId,
            @RequestBody NewLicenceDTO request
    ) {
        Plugin plugin = pluginService.getPlugin(pluginId);
        if (user == null || (plugin.ownerId() != user.getId() && !user.hasRole(UserRole.ADMIN))) {
            throw new ForbiddenException("You are not the owner of this plugin.");
        }

        PluginLicence licence = service.createLicence(user, pluginId, request);
        return assembler.toModel(licence);
    }

    @GetMapping("/{licence}")
    public EntityModel<PluginLicence> validate(@PathVariable String licence) {
        PluginLicence pluginLicence = service.validateLicence(service.uuidFromString(licence));

        return assembler.toModel(pluginLicence);
    }

    @PutMapping("/{licence}")
    public EntityModel<PluginLicence> updateLicence(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable String pluginId,
            @PathVariable String licence,
            @RequestBody UpdateLicenceDTO request
    ) {
        Plugin plugin = pluginService.getPlugin(pluginId);
        if (user == null || (plugin.ownerId() != user.getId() && !user.hasRole(UserRole.ADMIN))) {
            throw new ForbiddenException("You are not the owner of this plugin.");
        }

        PluginLicence pluginLicence = service.updateLicence(service.uuidFromString(licence), request);
        return assembler.toModel(pluginLicence);
    }

    @DeleteMapping("/{licence}")
    public void deleteLicence(@PathVariable String licence) {
        service.deleteLicence(service.uuidFromString(licence));
    }
}
