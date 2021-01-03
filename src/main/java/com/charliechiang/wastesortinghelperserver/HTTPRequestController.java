package com.charliechiang.wastesortinghelperserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@RestController
public class HTTPRequestController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WasteRepository wasteRepository;

    @PostMapping("/add-user")
    public @ResponseBody String addUser(@RequestParam(value = "id") Long id,
                                        @RequestParam(value = "name", defaultValue = "") String name) {
        Optional<User> referencedUser = userRepository.findById(id);
        if (referencedUser.isPresent()) {
            return "User already present.";
        } else {
            User newUser = new User(id, name);
            userRepository.save(newUser);
            return "Saved.";
        }

    }

    @GetMapping("/get-user")
    public @ResponseBody String getUser(@RequestParam(value = "id") Long id) {
        Optional<User> referencedUser = userRepository.findById(id);
        return referencedUser.map(User::getName).orElse(null);
    }

    @PostMapping("/add-waste")
    public @ResponseBody String addWaste(@RequestParam(value = "id") Long id,
                                         @RequestParam(value = "category") WasteCategory category,
                                         @RequestParam(value = "weight") Double weight,
                                         @RequestParam(value = "dustbinid") Long dustbinId) {
        Optional<User> referencedUser = userRepository.findById(id);
        if (referencedUser.isPresent()) {
            Waste newWaste = new Waste(referencedUser.get(), category, weight, dustbinId,LocalDateTime.now());
            wasteRepository.save(newWaste);
            return "Saved.";
        } else {
            return "User not present.";
        }
    }

    @GetMapping("/get-waste-list-top20")
    public ArrayList<Waste> getWasteListTop(@RequestParam(value = "id") Long id) {
        Optional<User> referencedUser = userRepository.findById(id);
        if (referencedUser.isPresent()) {
            return wasteRepository.findTop20ByUserOrderByIdDesc(referencedUser.get());
        } else {
            return null;
        }
    }

    @GetMapping("/get-waste-list-all")
    public ArrayList<Waste> getWasteListAll(@RequestParam(value = "id") Long id) {
        Optional<User> referencedUser = userRepository.findById(id);
        if (referencedUser.isPresent()) {
            return wasteRepository.findByUserOrderByIdDesc(referencedUser.get());
        } else {
            return null;
        }
    }

    @GetMapping("/get-credit")
    public int getCredit(@RequestParam(value = "id") Long id) {
        Optional<User> referencedUser = userRepository.findById(id);
        if (referencedUser.isPresent()) {
            return 666;
        } else {
            return 0;
        }
    }

}
