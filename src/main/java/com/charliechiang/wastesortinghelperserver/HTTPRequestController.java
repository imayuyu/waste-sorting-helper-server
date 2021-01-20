package com.charliechiang.wastesortinghelperserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

@RestController
public class HTTPRequestController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WasteRepository wasteRepository;
    @Autowired
    private DustbinRepository dustbinRepository;

    @PostMapping("/add-user")
    public @ResponseBody String addUser(@RequestParam(value = "id") Long id,
                                        @RequestParam(value = "name", defaultValue = "") String name) {
        Optional<User> referencedUser = userRepository.findById(id);
        if (referencedUser.isPresent()) {
            throw new ResourceConflictException();
        } else {
            User newUser = new User(id, name);
            userRepository.save(newUser);
            return id + " " + name + " saved.";
        }

    }

    @GetMapping("/get-user")
    public @ResponseBody String getUser(@RequestParam(value = "id") Long id) {
        Optional<User> referencedUser = userRepository.findById(id);
        if(referencedUser.isPresent()) {
            return referencedUser.get().getName();
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @PostMapping("/add-waste")
    public @ResponseBody String addWaste(@RequestParam(value = "id") Long id,
                                         @RequestParam(value = "category") WasteCategory category,
                                         @RequestParam(value = "weight") Double weight,
                                         @RequestParam(value = "dustbinid") Long dustbinId,
                                         @RequestParam(value = "time", defaultValue = "") String submissionTime) {
        Optional<User> referencedUser = userRepository.findById(id);
        if (referencedUser.isPresent()) {

            LocalDateTime submissionLocalDateTime;
            if (submissionTime.equals("")) {
                submissionLocalDateTime = LocalDateTime.now();
            } else {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                submissionLocalDateTime = LocalDateTime.parse(submissionTime, dateTimeFormatter);
            }
            Optional<Dustbin> referencedDustbin = dustbinRepository.findById(dustbinId);
            if (referencedDustbin.isPresent()) {
                Waste newWaste = new Waste(referencedUser.get(), category, weight, referencedDustbin.get(), submissionLocalDateTime);
                wasteRepository.save(newWaste);
                return "Saved.";
            } else {
                throw new ResourceNotFoundException();
            }


        } else {
            throw new ResourceNotFoundException();
        }
    }

    @GetMapping("/get-waste-list-top20")
    public ArrayList<Waste> getWasteListTop(@RequestParam(value = "id") Long id) {
        Optional<User> referencedUser = userRepository.findById(id);
        if (referencedUser.isPresent()) {
            return wasteRepository.findTop20ByUserOrderByIdDesc(referencedUser.get());
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @GetMapping("/get-waste-list-all")
    public ArrayList<Waste> getWasteListAll(@RequestParam(value = "id") Long id) {
        Optional<User> referencedUser = userRepository.findById(id);
        if (referencedUser.isPresent()) {
            return wasteRepository.findByUserOrderByIdDesc(referencedUser.get());
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @GetMapping("/get-credit")
    public int getCredit(@RequestParam(value = "id") Long id) {
        Optional<User> referencedUser = userRepository.findById(id);
        if (referencedUser.isPresent()) {
            return 666;
        } else {
            throw new ResourceNotFoundException();
        }
    }

    @PostMapping("/report-incorrect-categorization")
    public String reportIncorrectCategorization(@RequestParam(value = "dustbinid") Long dustbinId,
                                         @RequestParam(value = "time") String submissionTime) {
        Optional<Dustbin> referencedDustbin = dustbinRepository.findById(dustbinId);
        if(referencedDustbin.isPresent()) {
            LocalDateTime submissionLocalDateTime;
            if (submissionTime.equals("")) {
                submissionLocalDateTime = LocalDateTime.now();
            } else {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                submissionLocalDateTime = LocalDateTime.parse(submissionTime, dateTimeFormatter);
            }

            ArrayList<Waste> wasteInReferencedDustbin = wasteRepository.findTop5ByDustbinOrderByIdDesc(referencedDustbin.get());
            Waste suggestedWaste;
            for (Waste i : wasteInReferencedDustbin) {
                if (submissionLocalDateTime.isAfter(i.getTime())) {
                    suggestedWaste = i;
                    suggestedWaste.setCorrectlyCategorized(false);
                    return String.format("wasteId=%d, wasteTime=%s, userId=%d, userName=%s marked.",
                                         suggestedWaste.getId(),
                                         suggestedWaste.getTime().toString(),
                                         suggestedWaste.getUser().getId(),
                                         suggestedWaste.getUser().getName());
                }
            }
        }

        throw new ResourceNotFoundException();

    }


}


@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException extends RuntimeException {}

@ResponseStatus(HttpStatus.CONFLICT)
class ResourceConflictException extends RuntimeException {}