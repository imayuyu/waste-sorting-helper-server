package com.charliechiang.wastesortinghelperserver.model;

import com.charliechiang.wastesortinghelperserver.controller.WasteController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class WasteModelAssembler
        implements RepresentationModelAssembler<Waste, EntityModel<Waste>> {

    @Override
    public EntityModel<Waste> toModel(Waste waste) {

        return EntityModel.of(waste,
                              linkTo(methodOn(WasteController.class).getWasteSingle(waste.getId()))
                                      .withSelfRel());
    }
}
