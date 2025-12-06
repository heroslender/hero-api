package com.github.heroslender.hero_api.controller;

import com.github.heroslender.hero_api.controller.hateoas.PluginLicenceAssembler;
import com.github.heroslender.hero_api.model.PluginLicence;
import com.github.heroslender.hero_api.service.PluginLicenceService;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/plugins/{pluginId}/licence")
public class PluginLicenceController {
    private final PluginLicenceService service;
    private final PluginLicenceAssembler assembler;

    public PluginLicenceController(PluginLicenceService service, PluginLicenceAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @PostMapping()
    public void createLicence() {

    }

    @GetMapping("/{licence}")
    public EntityModel<PluginLicence> validate(@PathVariable String licence) {
        PluginLicence pluginLicence = service.validateLicence(UUID.fromString(licence));

        return assembler.toModel(pluginLicence);
    }

    @PutMapping("/{licence}")
    public void updateLicence(@PathVariable String licence) {

    }

    @DeleteMapping("/{licence}")
    public void deleteLicence(@PathVariable String licence) {

    }
}
