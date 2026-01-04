package com.github.heroslender.hero_api.controller.hateoas;

import com.github.heroslender.hero_api.controller.PluginController;
import com.github.heroslender.hero_api.model.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PluginAssembler implements RepresentationModelAssembler<Plugin, EntityModel<Plugin>> {
    @Override
    public @NonNull EntityModel<Plugin> toModel(@NonNull Plugin entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(PluginController.class).plugin(null, entity.id())).withSelfRel(),
                linkTo(methodOn(PluginController.class).plugins(null)).withRel("plugins")
        );
    }

    @Override
    public @NonNull CollectionModel<EntityModel<Plugin>> toCollectionModel(@NonNull Iterable<? extends Plugin> entities) {
        List<EntityModel<Plugin>> plugins = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .toList();

        return CollectionModel.of(plugins, linkTo(methodOn(PluginController.class).plugins(null)).withSelfRel());
    }
}
