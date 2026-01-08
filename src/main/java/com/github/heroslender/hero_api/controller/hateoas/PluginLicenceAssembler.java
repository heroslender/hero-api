package com.github.heroslender.hero_api.controller.hateoas;

import com.github.heroslender.hero_api.controller.PluginController;
import com.github.heroslender.hero_api.controller.PluginVersionController;
import com.github.heroslender.hero_api.model.PluginLicence;
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
public class PluginLicenceAssembler implements RepresentationModelAssembler<PluginLicence, EntityModel<PluginLicence>> {

    @Override
    public EntityModel<PluginLicence> toModel(PluginLicence entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(PluginVersionController.class).versions(entity.pluginId())).withRel("versions"),
                linkTo(methodOn(PluginController.class).plugin(null, entity.pluginId())).withRel("plugin")
        );
    }

    @Override
    public CollectionModel<EntityModel<PluginLicence>> toCollectionModel(Iterable<? extends PluginLicence> entities) {
        List<EntityModel<PluginLicence>> licences = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(licences, linkTo(methodOn(PluginController.class).plugins(null)).withSelfRel());
    }
}
