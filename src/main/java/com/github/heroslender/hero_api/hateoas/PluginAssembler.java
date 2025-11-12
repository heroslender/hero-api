package com.github.heroslender.hero_api.hateoas;

import com.github.heroslender.hero_api.controller.PluginController;
import com.github.heroslender.hero_api.dto.PluginDTO;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PluginAssembler implements RepresentationModelAssembler<PluginDTO, EntityModel<PluginDTO>> {
    @Override
    public EntityModel<PluginDTO> toModel(PluginDTO entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(PluginController.class).one(entity.id())).withSelfRel(),
                linkTo(methodOn(PluginController.class).all()).withRel("plugins")
        );
    }

    @Override
    public CollectionModel<EntityModel<PluginDTO>> toCollectionModel(Iterable<? extends PluginDTO> entities) {
        List<EntityModel<PluginDTO>> employees = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .toList();

        return CollectionModel.of(employees, linkTo(methodOn(PluginController.class).all()).withSelfRel());
    }
}
