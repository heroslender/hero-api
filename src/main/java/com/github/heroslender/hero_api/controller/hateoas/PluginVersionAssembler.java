package com.github.heroslender.hero_api.controller.hateoas;

import com.github.heroslender.hero_api.controller.PluginController;
import com.github.heroslender.hero_api.controller.PluginVersionController;
import com.github.heroslender.hero_api.model.PluginVersion;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PluginVersionAssembler implements RepresentationModelAssembler<PluginVersion, EntityModel<PluginVersion>> {

    @Override
    public EntityModel<PluginVersion> toModel(PluginVersion entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(PluginVersionController.class).version(entity.pluginId(), entity.tag())).withSelfRel(),
                linkTo(methodOn(PluginVersionController.class).versions(entity.pluginId())).withRel("versions"),
                linkTo(methodOn(PluginController.class).plugin(entity.pluginId())).withRel("plugin")
        );
    }

    @Override
    public CollectionModel<EntityModel<PluginVersion>> toCollectionModel(Iterable<? extends PluginVersion> entities) {
        List<EntityModel<PluginVersion>> employees = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(employees, linkTo(methodOn(PluginController.class).plugins()).withSelfRel());
    }
}
