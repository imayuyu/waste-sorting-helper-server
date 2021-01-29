package com.charliechiang.wastesortinghelperserver;

import org.hibernate.hql.internal.ast.tree.MethodNode;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class WasteController {
    private final DustbinRepository dustbinRepository;
    private final UserRepository userRepository;
    private final WasteRepository wasteRepository;

    private final WasteModelAssembler wasteModelAssembler;

    public WasteController(DustbinRepository dustbinRepository,
                           UserRepository userRepository,
                           WasteRepository wasteRepository,
                           WasteModelAssembler wasteModelAssembler) {
        this.dustbinRepository = dustbinRepository;
        this.userRepository = userRepository;
        this.wasteRepository = wasteRepository;
        this.wasteModelAssembler = wasteModelAssembler;
    }

    @PostMapping("/api/wastes")
    public ResponseEntity<?> addWaste(@RequestParam(value = "userid") Long userId,
                                      @RequestParam(value = "category") WasteCategory category,
                                      @RequestParam(value = "weight") Double weight,
                                      @RequestParam(value = "dustbinid") Long dustbinId,
                                      @RequestParam(value = "time", defaultValue = "") String submissionTime) {

        User referencedUser = userRepository.findById(userId)
                                            .orElseThrow(() -> new ResourceNotFoundException("User with ID="
                                                                                             + userId
                                                                                             + " could not be found."));
        Dustbin referencedDustbin = dustbinRepository.findById(dustbinId)
                                                     .orElseThrow(() -> new ResourceNotFoundException("Dustbin with ID="
                                                                                                      + dustbinId
                                                                                                      + " could not be found."));

        LocalDateTime submissionLocalDateTime;

        if (submissionTime.equals("")) {
            submissionLocalDateTime = LocalDateTime.now();
        } else {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            submissionLocalDateTime = LocalDateTime.parse(submissionTime, dateTimeFormatter);
        }

        EntityModel<Waste> entityModel =
                wasteModelAssembler.toModel(wasteRepository.save(new Waste(referencedUser,
                                                                           category,
                                                                           weight,
                                                                           referencedDustbin,
                                                                           submissionLocalDateTime)));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                                                 .toUri())
                             .body(entityModel);
    }

    @GetMapping("/api/wastes/{id}")
    public EntityModel<Waste> getWasteSingle(@PathVariable Long id) {

        return wasteModelAssembler.toModel(wasteRepository.findById(id)
                                                          .orElseThrow(() -> new ResourceNotFoundException("Waste " +
                                                                                                           "with ID="
                                                                                                           + id
                                                                                                           + " could not be found.")));
    }

    @GetMapping("/api/wastes")
    public CollectionModel<EntityModel<Waste>> getWasteAll() {

        List<EntityModel<Waste>> wastes =
                wasteRepository.findAll()
                               .stream()
                               .map(wasteModelAssembler::toModel)
                               .collect(Collectors.toList());

        return CollectionModel.of(wastes,
                                  linkTo(methodOn(WasteController.class).getWasteAll())
                                          .withSelfRel());
    }




    @PostMapping("/api/wastes/actions/report-incorrect-categorization")
    public ResponseEntity<?> reportIncorrectCategorization(@RequestParam(value = "dustbinid") Long dustbinId,
                                                           @RequestParam(value = "time") String submissionTime) {
        Dustbin referencedDustbin = dustbinRepository.findById(dustbinId)
                                                     .orElseThrow(() -> new ResourceNotFoundException("Dustbin with ID="
                                                                                                      + dustbinId
                                                                                                      + " could not be found."));

        LocalDateTime submissionLocalDateTime;
        if (submissionTime.equals("")) {
            submissionLocalDateTime = LocalDateTime.now();
        } else {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            submissionLocalDateTime = LocalDateTime.parse(submissionTime, dateTimeFormatter);
        }

        ArrayList<Waste> wasteInReferencedDustbin = wasteRepository.findTop5ByDustbinOrderByIdDesc(referencedDustbin);
        Waste suggestedWaste;
        for (Waste i : wasteInReferencedDustbin) {
            if (submissionLocalDateTime.isAfter(i.getTime())) {
                suggestedWaste = i;
                suggestedWaste.setCorrectlyCategorized(false);

                EntityModel<Waste> entityModel = wasteModelAssembler.toModel(wasteRepository.save(suggestedWaste));

                return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                                                         .toUri())
                                     .body(entityModel);
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
