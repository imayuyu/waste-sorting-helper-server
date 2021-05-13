package com.charliechiang.wastesortinghelperserver.model;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.charliechiang.wastesortinghelperserver.controller.SchoolController;
import com.charliechiang.wastesortinghelperserver.model.School;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class SchoolModelAssembler
        implements RepresentationModelAssembler<School, EntityModel<School>> {
    @Override
    public EntityModel<School> toModel(School school){
        return EntityModel.of(school,
                              linkTo(methodOn(SchoolController.class).getSchoolSingle(school.getId())).withSelfRel());
    }
}
