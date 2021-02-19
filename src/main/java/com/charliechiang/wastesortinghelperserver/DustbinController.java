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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class DustbinController {
    private final DustbinRepository dustbinRepository;
    private final WasteRepository wasteRepository;
    private final WasteModelAssembler wasteModelAssembler;
    private final DustbinModelAssembler dustbinModelAssembler;

    public DustbinController(DustbinRepository dustbinRepository,
                             WasteRepository wasteRepository,
                             WasteModelAssembler wasteModelAssembler,
                             DustbinModelAssembler dustbinModelAssembler) {
        this.dustbinRepository = dustbinRepository;
        this.wasteRepository = wasteRepository;
        this.wasteModelAssembler = wasteModelAssembler;
        this.dustbinModelAssembler = dustbinModelAssembler;
    }

    @GetMapping("/api/dustbins")
    public CollectionModel<EntityModel<Dustbin>> getDustbinAll() {

        List<EntityModel<Dustbin>> dustbins =
                dustbinRepository.findAll()
                                 .stream()
                                 .map(dustbinModelAssembler::toModel)
                                 .collect(Collectors.toList());

        return CollectionModel.of(dustbins,
                                  linkTo(methodOn(DustbinController.class).getDustbinAll())
                                          .withSelfRel());
    }

    @GetMapping("/api/dustbins/{id}")
    public EntityModel<Dustbin> getDustbinSingle(@PathVariable Long id) {

        Dustbin referencedDustbin =
                dustbinRepository.findById(id)
                                 .orElseThrow(() -> new ResourceNotFoundException("Dustbin with ID=" + id + " could " +
                                                                                  "not be found."));

        return dustbinModelAssembler.toModel(referencedDustbin);
    }

    @PostMapping("/api/dustbins")
    public ResponseEntity<?> addDustbin(@RequestBody Dustbin newDustbin) {

        EntityModel<Dustbin> entityModel =
                dustbinModelAssembler.toModel(dustbinRepository.save(newDustbin));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                                                 .toUri())
                             .body(entityModel);
    }

    @PutMapping("/api/dustbins/{id}")
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
                                 .orElseThrow(() -> new ResourceNotFoundException("Dustbin with ID="
                                                                                  + id
                                                                                  + " could not be found."));

        EntityModel<Dustbin> entityModel = dustbinModelAssembler.toModel(updatedDustbin);

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                                                 .toUri())
                             .body(entityModel);
    }

    @DeleteMapping("/api/dustbins/{id}")
    public ResponseEntity<?> deleteDustbin(@PathVariable Long id) {

        dustbinRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/dustbins/{id}/full")
    public ResponseEntity<?> updateDustbinFull(@PathVariable Long id,
                                               @RequestParam(value = "isfull") Boolean isFull) {

        Dustbin referencedDustbin = dustbinRepository.findById(id)
                                                     .orElseThrow(() -> new ResourceNotFoundException("Dustbin with ID="
                                                                                                      + id
                                                                                                      + " could not " +
                                                                                                      "be found."));

        referencedDustbin.setFull(isFull);

        EntityModel<Dustbin> entityModel =
                dustbinModelAssembler.toModel(dustbinRepository.save(referencedDustbin));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                                                 .toUri())
                             .body(entityModel);
    }

    @GetMapping("/api/dustbins/{id}/wastes")
    public CollectionModel<EntityModel<Waste>> getWasteAllByDustbin(@PathVariable Long id) {

        List<EntityModel<Waste>> wastes =
                wasteRepository.findByDustbinOrderByIdDesc(dustbinRepository.findById(id)
                                                                            .orElseThrow(() -> new ResourceNotFoundException("Dustbin with ID="
                                                                                                                             + id
                                                                                                                             + " could not be found.")))
                               .stream()
                               .map(wasteModelAssembler::toModel)
                               .collect(Collectors.toList());

        return CollectionModel.of(wastes,
                                  linkTo(methodOn(DustbinController.class).getWasteAllByDustbin(id))
                                          .withSelfRel());
    }

    @PostMapping("/api/dustbins/{id}/request")
    public ServerRequest sendOpenLidRequest(@PathVariable Long id,
                                                @RequestBody LidOpenRequestForm lidOpenRequestForm) {


        ServerRequest generatedRequest = ServerRequest.generateNewRequest(lidOpenRequestForm.userId,
                                                                          lidOpenRequestForm.dustbinId);
        try {
            WebSocketController.sendRequest(generatedRequest);
        } catch (Exception ex) {
            throw new ResourceNotFoundException(ex.getMessage());
        }

        EntityModel<ServerRequest> entityModel = EntityModel.of(generatedRequest);

        return generatedRequest;
    }

//    @GetMapping("/api/dustbins/{dustbinId}/request/{requestId}")
//    public EntityModel<ServerRequest> getRequestSingle(@PathVariable)
}

class LidOpenRequestForm {
    Long dustbinId;
    Long userId;

    public LidOpenRequestForm(Long dustbinId, Long userId) {
        this.dustbinId = dustbinId;
        this.userId = userId;
    }

    public Long getDustbinId() {
        return dustbinId;
    }

    public void setDustbinId(Long dustbinId) {
        this.dustbinId = dustbinId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}