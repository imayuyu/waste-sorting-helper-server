package com.charliechiang.wastesortinghelperserver.controller;

import com.charliechiang.wastesortinghelperserver.exception.ResourceNotFoundException;
import com.charliechiang.wastesortinghelperserver.model.School;
import com.charliechiang.wastesortinghelperserver.model.SchoolModelAssembler;
import com.charliechiang.wastesortinghelperserver.repository.SchoolRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class SchoolController {
    private final SchoolRepository schoolRepository;
    private final SchoolModelAssembler schoolModelAssembler;

    public SchoolController(SchoolRepository schoolRepository,
                            SchoolModelAssembler schoolModelAssembler){
        this.schoolRepository=schoolRepository;
        this.schoolModelAssembler=schoolModelAssembler;
    }

    @GetMapping("/api/schools")
    public CollectionModel<EntityModel<School>> getSchoolAll() {
        List<EntityModel<School>> schools =
                schoolRepository.findAll()
                .stream()
                .map(schoolModelAssembler::toModel)
        .collect(Collectors.toList());

        return CollectionModel.of(schools,
                                  linkTo(methodOn(SchoolController.class).getSchoolAll())
                                 .withSelfRel());
    }

    @GetMapping("/api/schools/{id}")
    public EntityModel<School> getSchoolSingle(@PathVariable Long id) {
        School referencedSchool =
                schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School with ID=" + id + " could not be found."));

        return schoolModelAssembler.toModel(referencedSchool);
    }

    @PostMapping("/api/schools")
    public ResponseEntity<?> addSchool(@RequestBody(required = false) School newSchool){
        EntityModel<School> entityModel=
                schoolModelAssembler.toModel(schoolRepository.save(newSchool));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                                     .toUri())
                .body(entityModel);
    }

    @DeleteMapping("/api/schools/{id}")
    public ResponseEntity<?> deleteDustbin(@PathVariable Long id){
        schoolRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
