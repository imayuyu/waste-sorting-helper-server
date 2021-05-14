package com.charliechiang.wastesortinghelperserver.controller;

import com.charliechiang.wastesortinghelperserver.exception.ResourceConflictException;
import com.charliechiang.wastesortinghelperserver.exception.ResourceNotFoundException;
import com.charliechiang.wastesortinghelperserver.model.School;
import com.charliechiang.wastesortinghelperserver.model.User;
import com.charliechiang.wastesortinghelperserver.model.UserModelAssembler;
import com.charliechiang.wastesortinghelperserver.model.Waste;
import com.charliechiang.wastesortinghelperserver.model.WasteCategory;
import com.charliechiang.wastesortinghelperserver.model.WasteModelAssembler;
import com.charliechiang.wastesortinghelperserver.repository.SchoolRepository;
import com.charliechiang.wastesortinghelperserver.repository.ServerSettingsRepository;
import com.charliechiang.wastesortinghelperserver.repository.UserRepository;
import com.charliechiang.wastesortinghelperserver.repository.WasteRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;
    private final WasteRepository wasteRepository;
    private final SchoolRepository schoolRepository;
    private final ServerSettingsRepository serverSettingsRepository;

    private final WasteModelAssembler wasteModelAssembler;
    private final UserModelAssembler userModelAssembler;

    private final PasswordEncoder passwordEncoder;

    private LocalDateTime lastUpdatedRankingTime = LocalDateTime.of(1970, 1, 1, 1, 1);

    private int collegeStudentCountCache;

    public UserController(UserRepository userRepository,
                          WasteRepository wasteRepository,
                          SchoolRepository schoolRepository,
                          ServerSettingsRepository serverSettingsRepository,
                          WasteModelAssembler wasteModelAssembler,
                          UserModelAssembler userModelAssembler,
                          PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.wasteRepository = wasteRepository;
        this.schoolRepository = schoolRepository;
        this.serverSettingsRepository = serverSettingsRepository;
        this.wasteModelAssembler = wasteModelAssembler;
        this.userModelAssembler = userModelAssembler;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/me")
    public EntityModel<User> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {

        User currentUser =
                userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new ResourceNotFoundException(
                        "User with username="
                        + userDetails.getUsername()
                        + " could not be found."));

        return userModelAssembler.toModel(currentUser);
    }

    @GetMapping("/me/wastes")
    public CollectionModel<EntityModel<Waste>> getCurrentUserWasteList(@AuthenticationPrincipal UserDetails userDetails,
                                                                       @RequestParam(value = "n", defaultValue = "0") Long n) {

        User currentUser =
                userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new ResourceNotFoundException(
                        "User with username="
                        + userDetails.getUsername()
                        + " could not be found."));

        List<EntityModel<Waste>> wastes;

        if (20 == n) {
            wastes = wasteRepository.findTop20ByUserOrderByIdDesc(currentUser)
                                    .stream()
                                    .map(wasteModelAssembler::toModel)
                                    .collect(Collectors.toList());
        } else if (0 == n) {
            wastes = wasteRepository.findByUserOrderByIdDesc(currentUser)
                                    .stream()
                                    .map(wasteModelAssembler::toModel)
                                    .collect(Collectors.toList());
        } else {
            wastes = Arrays.stream((Waste[]) Arrays.copyOfRange(wasteRepository.findByUserOrderByIdDesc(currentUser).toArray(),
                                                                0,
                                                                n.intValue()))
                           .map(wasteModelAssembler::toModel)
                           .collect(Collectors.toList());
        }

        return CollectionModel.of(wastes);
    }

    @GetMapping("")
    public CollectionModel<EntityModel<User>> getUserAll() {

        List<EntityModel<User>> users =
                userRepository.findAll()
                              .stream()
                              .map(userModelAssembler::toModel)
                              .collect(Collectors.toList());

        return CollectionModel.of(users,
                                  linkTo(methodOn(UserController.class).getUserAll()).withSelfRel());
    }

    private ResponseEntity<?> saveUser(UserCreationForm userCreationForm, List<String> roles) {
        User newUser = new User();

        newUser.setUsername(userCreationForm.getUsername());
        newUser.setPassword(this.passwordEncoder.encode(userCreationForm.getPassword()));
        newUser.setRealName(userCreationForm.getRealName());
        if (userCreationForm.getSchoolId() != -1) {
            newUser.setSchool(schoolRepository.findById(userCreationForm.getSchoolId()).orElseThrow(() -> new ResourceNotFoundException("School with ID=" + userCreationForm.getSchoolId() + " could not be found.")));
        }
        newUser.setTimeOfEnrollment(userCreationForm.getTimeOfEnrollment());
        newUser.setOpenId(userCreationForm.getOpenId());
        newUser.setRoles(roles);

        Optional<User> referencedUser = userRepository.findByUsername(userCreationForm.getUsername());
        referencedUser.ifPresent(user -> newUser.setId(user.getId()));

        EntityModel<User> entityModel = userModelAssembler.toModel(userRepository.save(newUser));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                                                 .toUri())
                             .body(entityModel);
    }

    @PostMapping("")
    public ResponseEntity<?> addUser(@RequestBody UserCreationForm userCreationForm) {

        Optional<User> referencedUser = userRepository.findByUsername(userCreationForm.getUsername());
        if (referencedUser.isPresent() || userCreationForm.getUsername().equals("me")) {
            throw new ResourceConflictException("User with username=" + userCreationForm.getUsername() + " already exists" +
                                                ".");
        }

        return saveUser(userCreationForm, Collections.singletonList("ROLE_USER"));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@AuthenticationPrincipal UserDetails userDetails,
                                               @RequestBody UserCreationForm userCreationForm) {

        if (!userCreationForm.getUsername().equals(userDetails.getUsername())) {
            throw new BadCredentialsException("You can only update your own account.");
        }

        return saveUser(userCreationForm, Collections.singletonList("ROLE_USER"));
    }

    @PutMapping("/{username}")
    public ResponseEntity<?> updateUser(@RequestBody UserCreationForm userCreationForm) {

        return saveUser(userCreationForm, Collections.singletonList("ROLE_USER"));
    }


    @GetMapping("/{username}")
    public EntityModel<User> getUserSingle(@PathVariable(value = "username") String username) {

        User referencedUser =
                userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User with username="
                                                                                                        + username
                                                                                                        + " could not be found."));

        return userModelAssembler.toModel(referencedUser);
    }

    //
    //    @PutMapping("/{username}")
    //    public ResponseEntity<?> updateUser(@PathVariable String username,
    //                                        @RequestParam(value = "name") String name) {
    //
    //        User updatedUser =
    //                userRepository.findByUsername()
    //                              .map(user -> {
    //                                  user.setUsername(name);
    //                                  return userRepository.save(user);
    //                              })
    //                              .orElseThrow(() -> new ResourceNotFoundException("User with ID="
    //                                                                               + id
    //                                                                               + " could not be found."));
    //
    //        EntityModel<User> entityModel = userModelAssembler.toModel(updatedUser);
    //
    //        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
    //                                                 .toUri())
    //                             .body(entityModel);
    //    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {

        userRepository.deleteByUsername(username);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{username}/wastes")
    public CollectionModel<EntityModel<Waste>> getWasteAllByUser(@PathVariable(value = "username") String username,
                                                                 @RequestParam(value = "n", defaultValue = "0") Long n) {


        User referencedUser = userRepository.findByUsername(username)
                                            .orElseThrow(() -> new ResourceNotFoundException("User with username="
                                                                                             + username
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

    @GetMapping("/{username}/credit")
    public int getCreditByUser(@PathVariable(value = "username") String username) throws Exception {

        User referencedUser = userRepository.findByUsername(username)
                                            .orElseThrow(() -> new ResourceNotFoundException("User with username="
                                                                                             + username
                                                                                             + " could not be found."));

        updateCredit(referencedUser, false);
        userRepository.save(referencedUser);


        return referencedUser.getCredit();
    }

    @GetMapping("/me/credit")
    public int getCreditByToken(@AuthenticationPrincipal UserDetails userDetails) throws Exception {

        User referencedUser = userRepository.findByUsername(userDetails.getUsername())
                                            .orElseThrow(() -> new ResourceNotFoundException("User with username="
                                                                                             + userDetails.getUsername()
                                                                                             + " could not be found."));

        updateCredit(referencedUser, false);
        userRepository.save(referencedUser);


        return referencedUser.getCredit();
    }

    public void updateCredit(User referencedUser, Boolean forceFullUpdate) throws Exception {
        // TODO: 总重超过减分

        if (referencedUser.getNeedFullCreditUpdate()) {
            forceFullUpdate = true;
            referencedUser.setNeedFullCreditUpdate(false);
        }

        int newCredit = 0;

        ArrayList<Waste> userWastes = null;

        if (forceFullUpdate) {
            userWastes = wasteRepository.findAllByUser(referencedUser);
        } else {
            if (referencedUser.getTimeLastUpdatedCredit().plusSeconds(ServerSettingsController.getServerSetting(
                    "creditUpdateDelay", serverSettingsRepository)).isAfter(LocalDateTime.now())) {
                return;
            }

            userWastes = wasteRepository.findAllByUserAndTimeIsAfter(referencedUser, referencedUser.getTimeLastUpdatedCredit());

            newCredit = referencedUser.getCredit();
        }

        // 是否正确分类的加分减分
        for (Waste i : userWastes) {
            if (i.getCorrectlyCategorized()) {
                if (i.getCategory() == WasteCategory.FOOD_WASTE
                    || i.getCategory() == WasteCategory.RECYCLABLE_WASTE) {
                    newCredit++;
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

    @GetMapping("/{username}/ranking")
    public PersonalRankingData getPersonalRanking(@PathVariable(value = "username") String username) throws Exception {
        // TODO: make async
        updateRanking();

        User referencedUser = userRepository.findByUsername(username)
                                            .orElseThrow(() -> new ResourceNotFoundException("User with username="
                                                                                             + username
                                                                                             + " could not be found."));

        School referencedSchool = schoolRepository.findById(referencedUser.getSchool().getId())
                                                  .orElseThrow(() -> new ResourceNotFoundException("User has an invalid school."));

        PersonalRankingData personalRankingData = new PersonalRankingData(referencedUser.getSchoolRanking(),
                                                                          referencedSchool.getStudentCount(),
                                                                          referencedUser.getCollegeRanking(),
                                                                          collegeStudentCountCache);

        return personalRankingData;
    }

    @GetMapping("/me/ranking")
    public PersonalRankingData getPersonalRankingByToken(@AuthenticationPrincipal UserDetails userDetails) throws Exception {
        // TODO: make async
        updateRanking();

        User referencedUser = userRepository.findByUsername(userDetails.getUsername())
                                            .orElseThrow(() -> new ResourceNotFoundException("User with username="
                                                                                             + userDetails.getUsername()
                                                                                             + " could not be found."));

        School referencedSchool = schoolRepository.findById(referencedUser.getSchool().getId())
                                                  .orElseThrow(() -> new ResourceNotFoundException("User has an invalid school."));

        PersonalRankingData personalRankingData = new PersonalRankingData(referencedUser.getSchoolRanking(),
                                                                          referencedSchool.getStudentCount(),
                                                                          referencedUser.getCollegeRanking(),
                                                                          collegeStudentCountCache);

        return personalRankingData;
    }

    public void updateRanking() throws Exception {

        // Do not do full update everytime
        if (lastUpdatedRankingTime.plusSeconds(ServerSettingsController.getServerSetting("rankingUpdateDelay", serverSettingsRepository))
                                  .isAfter(LocalDateTime.now())) {
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

        lastUpdatedRankingTime = LocalDateTime.now();
    }

    @PostMapping("/admins")
    public ResponseEntity<?> addAdmin(@RequestBody UserCreationForm userCreationForm) {

        Optional<User> referencedUser = userRepository.findByUsername(userCreationForm.getUsername());
        if (referencedUser.isPresent() || userCreationForm.getUsername().equals("me")) {
            throw new ResourceConflictException("User with username=" + userCreationForm.getUsername() + " already exists" + ".");
        }

        return saveUser(userCreationForm, Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
    }

    @GetMapping("/admins")
    public CollectionModel<EntityModel<User>> getAdminAll() {

        List<EntityModel<User>> admins =
                userRepository.findAllByRolesIsContaining("ROLE_ADMIN")
                              .stream()
                              .map(userModelAssembler::toModel)
                              .collect(Collectors.toList());

        return CollectionModel.of(admins,
                                  linkTo(methodOn(UserController.class).getUserAll()).withSelfRel());
    }
}

class UserCreationForm {

    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String realName;
    private String openId = "";
    private Long schoolId = -1L;
    private Short timeOfEnrollment = -1;

    public UserCreationForm() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
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
