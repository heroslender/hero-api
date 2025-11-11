package com.github.heroslender.hero_api.hateoas;

import com.github.heroslender.hero_api.controller.PluginController;
import com.github.heroslender.hero_api.dto.PluginVersionDTO;
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
public class PluginVersionDTOAssembler implements RepresentationModelAssembler<PluginVersionDTO, EntityModel<PluginVersionDTO>> {

    @Override
    public EntityModel<PluginVersionDTO> toModel(PluginVersionDTO entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(PluginController.class).one(entity.id())).withSelfRel(),
                linkTo(methodOn(PluginController.class).all()).withRel("plugins")
        );
    }

    @Override
    public CollectionModel<EntityModel<PluginVersionDTO>> toCollectionModel(Iterable<? extends PluginVersionDTO> entities) {
        List<EntityModel<PluginVersionDTO>> employees = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(employees, linkTo(methodOn(PluginController.class).all()).withSelfRel());
    }
}
