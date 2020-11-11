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
    public @ResponseBody String addUser(@RequestParam(value = "id") Long id, @RequestParam(value = "name", defaultValue = "") String name) {
        Optional<User> referencedUser = userRepository.findById(id);
        if (referencedUser.isPresent()) {
            return "User already present.";
        } else {
            User newUser = new User(id, name);
            userRepository.save(newUser);
            return "Saved.";
        }

    }

    @PostMapping("/add-waste")
    public @ResponseBody String addWaste(@RequestParam(value = "id") Long id, @RequestParam(value = "category") WasteCategory category) {
        Optional<User> referencedUser = userRepository.findById(id);
        if (referencedUser.isPresent()) {
            Waste newWaste = new Waste(referencedUser.get(), category, LocalDateTime.now());
            wasteRepository.save(newWaste);
            return "Saved.";
        } else {
            return "User not present.";
        }
    }

    @GetMapping("/get-waste-list")
    public ArrayList<Waste> getWasteList(@RequestParam(value = "id") Long id) {
        Optional<User> referencedUser = userRepository.findById(id);
        if (referencedUser.isPresent()) {
            ArrayList<Waste> wasteArrayList = wasteRepository.findByUser(referencedUser.get());
            Collections.reverse(wasteArrayList);
            return wasteArrayList;
        } else {
            return null;
        }
    }

    @GetMapping("/get-score")
    public int getScore(@RequestParam(value = "id") Long id) {
        Optional<User> referencedUser = userRepository.findById(id);
        if (referencedUser.isPresent()) {
            ArrayList<Waste> wasteArrayList = wasteRepository.findByUser(referencedUser.get());

            return wasteArrayList.size();
        } else {
            return 0;
        }
    }

}
