package com.charliechiang.wastesortinghelperserver;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

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
