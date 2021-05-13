package com.charliechiang.wastesortinghelperserver.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import com.charliechiang.wastesortinghelperserver.exception.ResourceNotFoundException;
import com.charliechiang.wastesortinghelperserver.model.School;
import com.charliechiang.wastesortinghelperserver.repository.SchoolRepository;
import com.charliechiang.wastesortinghelperserver.model.User;
import com.charliechiang.wastesortinghelperserver.model.UserModelAssembler;
import com.charliechiang.wastesortinghelperserver.repository.UserRepository;
import com.charliechiang.wastesortinghelperserver.model.Waste;
import com.charliechiang.wastesortinghelperserver.model.WasteCategory;
import com.charliechiang.wastesortinghelperserver.model.WasteModelAssembler;
import com.charliechiang.wastesortinghelperserver.repository.WasteRepository;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private final UserRepository userRepository;
    private final WasteRepository wasteRepository;
    private final SchoolRepository schoolRepository;

    private final WasteModelAssembler wasteModelAssembler;
    private final UserModelAssembler userModelAssembler;

    private final LocalDateTime lastUpdatedRankingTime = LocalDateTime.of(1970, 1, 1, 1, 1);

    private final long rankingUpdateDelay = 1;
    private final TemporalUnit rankingUpdateDelayUnit = ChronoUnit.MINUTES;
    private int collegeStudentCountCache;

    public UserController(UserRepository userRepository,
                          WasteRepository wasteRepository,
                          SchoolRepository schoolRepository,
                          WasteModelAssembler wasteModelAssembler,
                          UserModelAssembler userModelAssembler) {

        this.userRepository = userRepository;
        this.wasteRepository = wasteRepository;
        this.schoolRepository = schoolRepository;
        this.wasteModelAssembler = wasteModelAssembler;
        this.userModelAssembler = userModelAssembler;
    }


    @PostMapping("/api/users")
    public ResponseEntity<?> addUser(@RequestBody UserCreationForm userCreationForm) {

        User newUser = new User();
        // TODO: set password
        newUser.setId(userCreationForm.getId());
        newUser.setName(userCreationForm.getName());
        newUser.setSchool(schoolRepository.findById(userCreationForm.getSchoolId()).orElseThrow(() -> new ResourceNotFoundException("School with ID=" + userCreationForm.getSchoolId() + " could not be found.")));
        newUser.setTimeOfEnrollment(userCreationForm.getTimeOfEnrollment());
        newUser.setOpenId(userCreationForm.getOpenId());

        EntityModel<User> entityModel =
                userModelAssembler.toModel(userRepository.save(newUser));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                                                 .toUri())
                             .body(entityModel);
    }


    @GetMapping("/api/users/{id}")
    public EntityModel<User> getUserSingle(@PathVariable(value = "id") Long id) {

        User referencedUser =
                userRepository.findById(id)
                              .orElseThrow(() -> new ResourceNotFoundException("User with ID="
                                                                               + id
                                                                               + " could not be found."));

        return userModelAssembler.toModel(referencedUser);
    }

    // TODO: enable updating on all fields in User
    @PutMapping("/api/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @RequestParam(value = "name") String name) {

        User updatedUser =
                userRepository.findById(id)
                              .map(user -> {
                                  user.setName(name);
                                  return userRepository.save(user);
                              })
                              .orElseThrow(() -> new ResourceNotFoundException("User with ID="
                                                                               + id
                                                                               + " could not be found."));

        EntityModel<User> entityModel = userModelAssembler.toModel(updatedUser);

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                                                 .toUri())
                             .body(entityModel);
    }

    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        userRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/api/users/{userid}/wastes")
    public CollectionModel<EntityModel<Waste>> getWasteAllByUser(@PathVariable(value = "userid") Long userId,
                                                                 @RequestParam(value = "n", defaultValue = "0") Long n) {

        User referencedUser = userRepository.findById(userId)
                                            .orElseThrow(() -> new ResourceNotFoundException("User with ID="
                                                                                             + userId
                                                                                             + " could not be found."));

        List<EntityModel<Waste>> wastes;

        if (20 == n) {
            wastes = wasteRepository.findTop20ByUserOrderByIdDesc(referencedUser)
                                    .stream()
                                    .map(wasteModelAssembler::toModel)
                                    .collect(Collectors.toList());
        } else if (0 == n) {
            wastes = wasteRepository.findByUserOrderByIdDesc(referencedUser)
                                    .stream()
                                    .map(wasteModelAssembler::toModel)
                                    .collect(Collectors.toList());
        } else {
            wastes = Arrays.stream((Waste[]) Arrays.copyOfRange(wasteRepository.findByUserOrderByIdDesc(referencedUser).toArray(),
                                                                0,
                                                                n.intValue()))
                           .map(wasteModelAssembler::toModel)
                           .collect(Collectors.toList());
        }

        return CollectionModel.of(wastes);
    }

    // TODO: only update credit every once in a while, or only a new waste has been submitted?
    @GetMapping("/api/users/{userid}/credit")
    public int getCreditByUser(@PathVariable(value = "userid") Long userId) {

        User referencedUser = userRepository.findById(userId)
                                            .orElseThrow(() -> new ResourceNotFoundException("User with ID="
                                                                                             + userId
                                                                                             + " could not be found."));

        updateCredit(referencedUser);
        userRepository.save(referencedUser);


        return referencedUser.getCredit();
    }

    public void updateCredit(User referencedUser) {
        // TODO: lazy update
        // TODO: Check if this updates
        // TODO: 总重超过减分
        int newCredit = referencedUser.getCredit();

        //        if (referencedUser.getTimeLastUpdatedCredit().plusHours(1).isAfter(LocalDateTime.now()))
        //            return;

        ArrayList<Waste> userWastes = wasteRepository.findAllByUserAndTimeIsAfter(referencedUser,
                                                                                  referencedUser.getTimeLastUpdatedCredit());
        // 是否正确分类的加分减分
        for (Waste i : userWastes) {
            if (i.getCorrectlyCategorized()) {
                if (i.getCategory() == WasteCategory.FOOD_WASTE
                    || i.getCategory() == WasteCategory.RECYCLABLE_WASTE) {
                    newCredit++;
                } else {
                    newCredit--;
                }
            } else {
                newCredit--;
            }
        }
        // 总重超过的减分
        //        LocalDateTime startTimeOfTheInterval = LocalDateTime
        //        ArrayList<Waste> userWastesBetweenInterval = wasteRepository.findAllByUserAndTimeIsAfter(this, );


        if (newCredit < 0) {
            newCredit = 0;
        }

        referencedUser.setCredit(newCredit);
        referencedUser.setTimeLastUpdatedCredit(LocalDateTime.now());
    }

    @GetMapping("/api/users/{userid}/ranking")
    public PersonalRankingData getPersonalRanking(@PathVariable(value = "userid") Long userId) {
        // TODO: make async
        updateRanking();

        User referencedUser = userRepository.findById(userId)
                                            .orElseThrow(() -> new ResourceNotFoundException("User with ID="
                                                                                             + userId
                                                                                             + " could not be found."));

        School referencedSchool = schoolRepository.findById(referencedUser.getSchool().getId())
                                                  .orElseThrow(() -> new ResourceNotFoundException("User has an invalid school."));

        PersonalRankingData personalRankingData = new PersonalRankingData(referencedUser.getSchoolRanking(),
                                                                          referencedSchool.getStudentCount(),
                                                                          referencedUser.getCollegeRanking(),
                                                                          collegeStudentCountCache);

        return personalRankingData;
    }

    public void updateRanking() {
        if (lastUpdatedRankingTime.plus(rankingUpdateDelay, rankingUpdateDelayUnit).isAfter(LocalDateTime.now())) {
            return;
        }

        ArrayList<User> collegeRanking = userRepository.findAllByOrderByCreditDesc();
        HashMap<School, ArrayList<User>> schoolRankings = new HashMap<>();
        List<School> schools = schoolRepository.findAll();

        for (School i : schools) {
            schoolRankings.put(i, new ArrayList<>());
        }

        // update college ranking and split users to schools
        for (int i = 0; i < collegeRanking.size(); i++) {
            collegeRanking.get(i).setCollegeRanking(i + 1);
            schoolRankings.get(collegeRanking.get(i).getSchool()).add(collegeRanking.get(i));
        }
        collegeStudentCountCache = collegeRanking.size();

        // update school ranking
        for (School i : schools) {
            ArrayList<User> thisSchoolRanking = schoolRankings.get(i);
            i.setStudentCount(thisSchoolRanking.size());
            for (int j = 0; j < thisSchoolRanking.size(); j++) {
                thisSchoolRanking.get(j).setSchoolRanking(j + 1);
                userRepository.save(thisSchoolRanking.get(j));
            }
        }
    }
}

class UserCreationForm {
    private Long id;
    private String name;
    private String password;
    private String openId;
    private Long schoolId;
    private Short timeOfEnrollment;

    public UserCreationForm() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Short getTimeOfEnrollment() {
        return timeOfEnrollment;
    }

    public void setTimeOfEnrollment(Short timeOfEnrollment) {
        this.timeOfEnrollment = timeOfEnrollment;
    }
}

class PersonalRankingData {
    private Integer schoolRanking;
    private Integer collegeRanking;
    private Integer schoolStudentCount;
    private Integer collegeStudentCount;

    public PersonalRankingData(Integer schoolRanking,
                               Integer schoolStudentCount,
                               Integer collegeRanking,
                               Integer collegeStudentCount) {
        this.schoolRanking = schoolRanking;
        this.collegeRanking = collegeRanking;
        this.schoolStudentCount = schoolStudentCount;
        this.collegeStudentCount = collegeStudentCount;
    }

    public Integer getSchoolRanking() {
        return schoolRanking;
    }

    public void setSchoolRanking(Integer schoolRanking) {
        this.schoolRanking = schoolRanking;
    }

    public Integer getCollegeRanking() {
        return collegeRanking;
    }

    public void setCollegeRanking(Integer collegeRanking) {
        this.collegeRanking = collegeRanking;
    }

    public Integer getSchoolStudentCount() {
        return schoolStudentCount;
    }

    public void setSchoolStudentCount(Integer schoolStudentCount) {
        this.schoolStudentCount = schoolStudentCount;
    }

    public Integer getCollegeStudentCount() {
        return collegeStudentCount;
    }

    public void setCollegeStudentCount(Integer collegeStudentCount) {
        this.collegeStudentCount = collegeStudentCount;
    }
}
