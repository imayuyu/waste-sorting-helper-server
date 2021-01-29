package com.charliechiang.wastesortinghelperserver;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class DustbinModelAssembler
        implements RepresentationModelAssembler<Dustbin, EntityModel<Dustbin>> {
    @Override
    public EntityModel<Dustbin> toModel(Dustbin dustbin) {
        return EntityModel.of(dustbin,
                              linkTo(methodOn(DustbinController.class).getDustbinSingle(dustbin.getId()))
                                      .withSelfRel(),
                              linkTo(methodOn(DustbinController.class).getDustbinAll())
                                      .withRel("dustbins"));
    }
}
