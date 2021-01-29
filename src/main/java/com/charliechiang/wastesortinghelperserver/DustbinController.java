package com.charliechiang.wastesortinghelperserver;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class DustbinController {
    private final DustbinRepository dustbinRepository;
    private final DustbinModelAssembler dustbinModelAssembler;

    public DustbinController(DustbinRepository dustbinRepository,
                          DustbinModelAssembler dustbinModelAssembler) {
        this.dustbinRepository = dustbinRepository;
        this.dustbinModelAssembler = dustbinModelAssembler;
    }

    @GetMapping("/dustbins")
    public CollectionModel<EntityModel<Dustbin>> getDustbinAll() {

        List<EntityModel<Dustbin>> dustbins =
                dustbinRepository.findAll()
                                 .stream()
                                 .map(dustbinModelAssembler::toModel)
                                 .collect(Collectors.toList());

        return CollectionModel.of(dustbins, linkTo(methodOn(DustbinController.class).getDustbinAll())
                .withSelfRel());
    }

    @GetMapping("/dustbins/{id}")
    public EntityModel<Dustbin> getDustbinSingle(@PathVariable Long id) {

        Dustbin referencedDustbin =
                dustbinRepository.findById(id)
                                 .orElseThrow(() -> new ResourceNotFoundException());

        return dustbinModelAssembler.toModel(referencedDustbin);
    }

    @PostMapping("/dustbins")
    public ResponseEntity<?> addDustbin(@RequestBody Dustbin newDustbin) {

        EntityModel<Dustbin> entityModel =
                dustbinModelAssembler.toModel(dustbinRepository.save(newDustbin));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                                                 .toUri())
                             .body(entityModel);
    }

    @PutMapping("/dustbins/{id}")
    public ResponseEntity<?> updateDustbin(@PathVariable(value = "id") Long id,
                                           @RequestBody Dustbin newDustbin) {

        Dustbin updatedDustbin =
                dustbinRepository.findById(id)
                                 .map(dustbin -> {
                                     dustbin.setName(newDustbin.getName());
                                     dustbin.setLongitude(newDustbin.getLongitude());
                                     dustbin.setLatitude(newDustbin.getLatitude());
                                     dustbin.setFull(newDustbin.getFull());
                                     return dustbinRepository.save(dustbin);
                                 })
                                 .orElseGet(() -> {
                                     newDustbin.setId(id);
                                     return dustbinRepository.save(newDustbin);
                                 });

        EntityModel<Dustbin> entityModel = dustbinModelAssembler.toModel(updatedDustbin);

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                                                 .toUri())
                             .body(entityModel);
    }

    @DeleteMapping("/dustbins/{id}")
    public ResponseEntity<?> deleteDustbin(@PathVariable Long id) {

        dustbinRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
