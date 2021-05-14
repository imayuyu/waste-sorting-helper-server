package com.charliechiang.wastesortinghelperserver.model;

import com.charliechiang.wastesortinghelperserver.controller.DustbinController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DustbinModelAssembler
        implements RepresentationModelAssembler<Dustbin, EntityModel<Dustbin>> {

    @Override
    public EntityModel<Dustbin> toModel(Dustbin dustbin) {

        return EntityModel.of(dustbin,
                              linkTo(methodOn(DustbinController.class).getDustbinSingle(dustbin.getId())).withSelfRel(),
                              linkTo(methodOn(DustbinController.class).getDustbinAll()).withRel("dustbins"));
    }
}
