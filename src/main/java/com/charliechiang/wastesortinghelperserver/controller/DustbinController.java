package com.charliechiang.wastesortinghelperserver.controller;

import com.charliechiang.wastesortinghelperserver.exception.ResourceNotFoundException;
import com.charliechiang.wastesortinghelperserver.model.Dustbin;
import com.charliechiang.wastesortinghelperserver.model.DustbinModelAssembler;
import com.charliechiang.wastesortinghelperserver.model.Waste;
import com.charliechiang.wastesortinghelperserver.model.WasteModelAssembler;
import com.charliechiang.wastesortinghelperserver.repository.DustbinRepository;
import com.charliechiang.wastesortinghelperserver.repository.UserRepository;
import com.charliechiang.wastesortinghelperserver.repository.WasteRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/dustbins")
public class DustbinController {

    private final DustbinRepository dustbinRepository;
    private final WasteRepository wasteRepository;
    private final UserRepository userRepository;

    private final WasteModelAssembler wasteModelAssembler;
    private final DustbinModelAssembler dustbinModelAssembler;

    public DustbinController(DustbinRepository dustbinRepository,
                             WasteRepository wasteRepository,
                             UserRepository userRepository,
                             WasteModelAssembler wasteModelAssembler,
                             DustbinModelAssembler dustbinModelAssembler) {

        this.dustbinRepository = dustbinRepository;
        this.wasteRepository = wasteRepository;
        this.userRepository = userRepository;
        this.wasteModelAssembler = wasteModelAssembler;
        this.dustbinModelAssembler = dustbinModelAssembler;
    }

    @GetMapping("")
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

    @GetMapping("/{id}")
    public EntityModel<Dustbin> getDustbinSingle(@PathVariable Long id) {

        Dustbin referencedDustbin =
                dustbinRepository.findById(id)
                                 .orElseThrow(() -> new ResourceNotFoundException("Dustbin with ID=" + id + " could " +
                                                                                  "not be found."));

        return dustbinModelAssembler.toModel(referencedDustbin);
    }

    @PostMapping("")
    public ResponseEntity<?> addDustbin(@RequestBody Dustbin newDustbin) {

        EntityModel<Dustbin> entityModel =
                dustbinModelAssembler.toModel(dustbinRepository.save(newDustbin));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                                                 .toUri())
                             .body(entityModel);
    }

    //    @PutMapping("/{id}")
    //    public ResponseEntity<?> updateDustbin(@PathVariable(value = "id") Long id,
    //                                           @RequestBody Dustbin newDustbin) {
    //
    //        Dustbin updatedDustbin =
    //                dustbinRepository.findById(id)
    //                                 .map(dustbin -> {
    //                                     dustbin.setName(newDustbin.getName());
    //                                     dustbin.setLongitude(newDustbin.getLongitude());
    //                                     dustbin.setLatitude(newDustbin.getLatitude());
    //                                     dustbin.setFull(newDustbin.getFull());
    //                                     return dustbinRepository.save(dustbin);
    //                                 })
    //                                 .orElseThrow(() -> new ResourceNotFoundException("Dustbin with ID="
    //                                                                                  + id
    //                                                                                  + " could not be found."));
    //
    //        EntityModel<Dustbin> entityModel = dustbinModelAssembler.toModel(updatedDustbin);
    //
    //        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
    //                                                 .toUri())
    //                             .body(entityModel);
    //    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDustbin(@PathVariable Long id) {

        dustbinRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/full")
    public ResponseEntity<?> updateDustbinFull(@PathVariable Long id,
                                               @RequestBody DustbinFullForm dustbinFullForm) {

        Dustbin referencedDustbin = dustbinRepository.findById(id)
                                                     .orElseThrow(() -> new ResourceNotFoundException("Dustbin with ID="
                                                                                                      + id
                                                                                                      + " could not " +
                                                                                                      "be found."));

        referencedDustbin.setFoodWasteFull(dustbinFullForm.getFoodWasteFull());
        referencedDustbin.setHazardousWasteFull(dustbinFullForm.getHazardousWasteFull());
        referencedDustbin.setRecyclableWasteFull(dustbinFullForm.getRecyclableWasteFull());
        referencedDustbin.setResidualWasteFull(dustbinFullForm.getResidualWasteFull());

        EntityModel<Dustbin> entityModel =
                dustbinModelAssembler.toModel(dustbinRepository.save(referencedDustbin));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                             .body(entityModel);
    }

    @GetMapping("/{id}/wastes")
    public CollectionModel<EntityModel<Waste>> getWasteAllByDustbin(@PathVariable Long id) {

        List<EntityModel<Waste>> wastes =
                wasteRepository.findByDustbinOrderByTimeDesc(dustbinRepository.findById(id)
                                                                              .orElseThrow(() -> new ResourceNotFoundException("Dustbin with ID="
                                                                                                                             + id
                                                                                                                             + " could not be found.")))
                               .stream()
                               .map(wasteModelAssembler::toModel)
                               .collect(Collectors.toList());

        return CollectionModel.of(wastes, linkTo(methodOn(DustbinController.class).getWasteAllByDustbin(id))
                .withSelfRel());
    }

    //    @PostMapping("/{id}/requests")
    //    public ResponseEntity<?> sendOpenLidRequest(@PathVariable Long id,
    //                                                @RequestBody LidOpenRequestForm lidOpenRequestForm) {
    //
    //        userRepository.findByUsername(lidOpenRequestForm.getUsername())
    //                      .orElseThrow(() -> new ResourceNotFoundException("User with username="
    //                                                                       + lidOpenRequestForm.getUsername()
    //                                                                       + " could not be found."));
    //
    //        ServerRequest generatedRequest = ServerRequest.generateNewRequest(lidOpenRequestForm.getUsername(),
    //                                                                          lidOpenRequestForm.getDustbinId());
    //        try {
    //            WebSocketController.sendRequest(generatedRequest);
    //        } catch (Exception ex) {
    //            throw new ResourceNotFoundException(ex.getMessage());
    //        }
    //
    //        EntityModel<ServerRequest> entityModel = EntityModel.of(generatedRequest,
    //                                                                linkTo(methodOn(DustbinController.class).getRequestSingle(generatedRequest.getDustbinId(),
    //                                                                                                                          generatedRequest.getRequestId())).withSelfRel());
    //
    //        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    //    }

    @PostMapping("/{id}/requests")
    public ResponseEntity<?> sendOpenLidRequestByToken(@PathVariable Long id,
                                                       @AuthenticationPrincipal UserDetails userDetails) {

        userRepository.findByUsername(userDetails.getUsername())
                      .orElseThrow(() -> new ResourceNotFoundException("User with username="
                                                                       + userDetails.getUsername()
                                                                       + " could not be found."));

        ServerRequest generatedRequest = ServerRequest.generateNewRequest(userDetails.getUsername(), id);

        try {
            WebSocketController.sendRequest(generatedRequest);
        } catch (Exception ex) {
            throw new ResourceNotFoundException(ex.getMessage());
        }

        EntityModel<ServerRequest> entityModel =
                EntityModel.of(generatedRequest,
                               linkTo(methodOn(DustbinController.class).getRequestSingle(generatedRequest.getDustbinId(),
                                                                                         generatedRequest.getRequestId())).withSelfRel());

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @GetMapping("/{dustbinId}/requests/{requestId}")
    public EntityModel<ServerRequest> getRequestSingle(@PathVariable Long dustbinId,
                                                       @PathVariable Long requestId) {

        return EntityModel.of(WebSocketController.getRequest(dustbinId, requestId));
    }


}

class LidOpenRequestForm {

    private Long dustbinId;
    private String username;

    public LidOpenRequestForm(Long dustbinId, String username) {
        this.dustbinId = dustbinId;
        this.username = username;
    }

    public Long getDustbinId() {
        return dustbinId;
    }

    public void setDustbinId(Long dustbinId) {
        this.dustbinId = dustbinId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

class DustbinFullForm {

    private Boolean isHazardousWasteFull;
    private Boolean isRecyclableWasteFull;
    private Boolean isFoodWasteFull;
    private Boolean isResidualWasteFull;

    public DustbinFullForm() {

    }

    public Boolean getHazardousWasteFull() {
        return isHazardousWasteFull;
    }

    public void setHazardousWasteFull(Boolean hazardousWasteFull) {
        isHazardousWasteFull = hazardousWasteFull;
    }

    public Boolean getRecyclableWasteFull() {
        return isRecyclableWasteFull;
    }

    public void setRecyclableWasteFull(Boolean recyclableWasteFull) {
        isRecyclableWasteFull = recyclableWasteFull;
    }

    public Boolean getFoodWasteFull() {
        return isFoodWasteFull;
    }

    public void setFoodWasteFull(Boolean foodWasteFull) {
        isFoodWasteFull = foodWasteFull;
    }

    public Boolean getResidualWasteFull() {
        return isResidualWasteFull;
    }

    public void setResidualWasteFull(Boolean residualWasteFull) {
        isResidualWasteFull = residualWasteFull;
    }
}