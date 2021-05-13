package com.charliechiang.wastesortinghelperserver.model;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.charliechiang.wastesortinghelperserver.controller.WasteController;
import com.charliechiang.wastesortinghelperserver.model.Waste;
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
