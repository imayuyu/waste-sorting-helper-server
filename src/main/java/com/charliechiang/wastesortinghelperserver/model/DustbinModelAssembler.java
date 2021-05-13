package com.charliechiang.wastesortinghelperserver.model;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.charliechiang.wastesortinghelperserver.controller.DustbinController;
import com.charliechiang.wastesortinghelperserver.model.Dustbin;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

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
