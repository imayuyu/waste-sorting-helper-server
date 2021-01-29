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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private final UserRepository userRepository;
    private final WasteRepository wasteRepository;

    private final WasteModelAssembler wasteModelAssembler;
    private final UserModelAssembler userModelAssembler;


    public UserController(UserRepository userRepository,
                          WasteRepository wasteRepository,
                          WasteModelAssembler wasteModelAssembler,
                          UserModelAssembler userModelAssembler) {

        this.userRepository = userRepository;
        this.wasteRepository = wasteRepository;
        this.wasteModelAssembler = wasteModelAssembler;
        this.userModelAssembler = userModelAssembler;
    }


    @PostMapping("/api/users")
    public ResponseEntity<?> addUser(@RequestParam(value = "id") Long id,
                                     @RequestParam(value = "name", defaultValue = "") String name) {

        Optional<User> referencedUser = userRepository.findById(id);

        if (referencedUser.isPresent()) {
            throw new ResourceConflictException("User with ID=" + id + " already exists.");
        } else {
            EntityModel<User> entityModel =
                    userModelAssembler.toModel(userRepository.save(new User(id, name)));

            return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                                                     .toUri())
                                 .body(entityModel);
        }
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

}
