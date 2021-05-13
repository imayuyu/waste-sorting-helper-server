package com.charliechiang.wastesortinghelperserver.controller;

import com.charliechiang.wastesortinghelperserver.model.Dustbin;
import com.charliechiang.wastesortinghelperserver.repository.DustbinRepository;
import com.charliechiang.wastesortinghelperserver.exception.ResourceNotFoundException;
import com.charliechiang.wastesortinghelperserver.model.User;
import com.charliechiang.wastesortinghelperserver.repository.UserRepository;
import com.charliechiang.wastesortinghelperserver.model.Waste;
import com.charliechiang.wastesortinghelperserver.model.WasteCategory;
import com.charliechiang.wastesortinghelperserver.model.WasteModelAssembler;
import com.charliechiang.wastesortinghelperserver.repository.WasteRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class WasteController {
    private final DustbinRepository dustbinRepository;
    private final UserRepository userRepository;
    private final WasteRepository wasteRepository;

    private final WasteModelAssembler wasteModelAssembler;

    private final UserController userController;

    public WasteController(DustbinRepository dustbinRepository,
                           UserRepository userRepository,
                           WasteRepository wasteRepository,
                           WasteModelAssembler wasteModelAssembler,
                           UserController userController) {
        this.dustbinRepository = dustbinRepository;
        this.userRepository = userRepository;
        this.wasteRepository = wasteRepository;
        this.wasteModelAssembler = wasteModelAssembler;
        this.userController=userController;
    }

    @PostMapping("/api/wastes")
    public ResponseEntity<?> addWaste(@RequestBody WasteForm wasteForm) {

        User referencedUser = userRepository.findById(wasteForm.getUserId())
                                            .orElseThrow(() -> new ResourceNotFoundException("User with ID="
                                                                                             + wasteForm.getUserId()
                                                                                             + " could not be found."));
        Dustbin referencedDustbin = dustbinRepository.findById(wasteForm.getDustbinId())
                                                     .orElseThrow(() -> new ResourceNotFoundException("Dustbin with ID="
                                                                                                      + wasteForm.getDustbinId()
                                                                                                      + " could not be found."));

        LocalDateTime submissionLocalDateTime;

        if (wasteForm.getTime().equals("")) {
            submissionLocalDateTime = LocalDateTime.now();
        } else {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            submissionLocalDateTime = LocalDateTime.parse(wasteForm.getTime(), dateTimeFormatter);
        }

        EntityModel<Waste> entityModel =
                wasteModelAssembler.toModel(wasteRepository.save(new Waste(referencedUser,
                                                                           wasteForm.getCategory(),
                                                                           wasteForm.getWeight(),
                                                                           referencedDustbin,
                                                                           submissionLocalDateTime)));

        // TODO: make async
        userController.updateCredit(referencedUser);

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


class WasteForm {
    private WasteCategory category;
    private String time = "";
    private Long userId;
    private Double weight;
    private Long dustbinId;
    private Boolean isCorrectlyCategorized;

    public WasteForm() {

    }

    public WasteForm(Long userId,
                     Long dustbinId,
                     Double weight,
                     WasteCategory category,
                     String time,
                     Boolean isCorrectlyCategorized) {
        this.userId = userId;
        this.category = category;
        this.time = time;
        this.weight = weight;
        this.dustbinId = dustbinId;
        this.isCorrectlyCategorized = isCorrectlyCategorized;
    }

    public WasteForm(Long userId,
                     Long dustbinId,
                     Double weight,
                     WasteCategory category,
                     Boolean isCorrectlyCategorized
                    ) {
        this.userId = userId;
        this.category = category;
        this.weight = weight;
        this.dustbinId = dustbinId;
        this.isCorrectlyCategorized = isCorrectlyCategorized;
    }

    public WasteCategory getCategory() {
        return category;
    }

    public void setCategory(WasteCategory category) {
        this.category = category;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Long getDustbinId() {
        return dustbinId;
    }

    public void setDustbinId(Long dustbinId) {
        this.dustbinId = dustbinId;
    }

    public Boolean getCorrectlyCategorized() {
        return isCorrectlyCategorized;
    }

    public void setCorrectlyCategorized(Boolean correctlyCategorized) {
        isCorrectlyCategorized = correctlyCategorized;
    }
}