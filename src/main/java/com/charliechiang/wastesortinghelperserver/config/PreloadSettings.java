package com.charliechiang.wastesortinghelperserver.config;

import com.charliechiang.wastesortinghelperserver.model.School;
import com.charliechiang.wastesortinghelperserver.model.ServerSetting;
import com.charliechiang.wastesortinghelperserver.model.User;
import com.charliechiang.wastesortinghelperserver.repository.SchoolRepository;
import com.charliechiang.wastesortinghelperserver.repository.ServerSettingsRepository;
import com.charliechiang.wastesortinghelperserver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@Configuration
public class PreloadSettings {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreloadSettings.class);

    @Bean
    CommandLineRunner loadData(ServerSettingsRepository serverSettingsRepository,
                               UserRepository userRepository,
                               SchoolRepository schoolRepository,
                               PasswordEncoder passwordEncoder) {

        int savedSettingsCount = 0;

        ArrayList<ServerSetting> defaultSettings = new ArrayList<>();
        defaultSettings.add(new ServerSetting("creditUpdateDelay", "120", "integer")); //second
        defaultSettings.add(new ServerSetting("rankingUpdateDelay", "3600", "integer")); //second
        defaultSettings.add(new ServerSetting("wasteWeightThreshold", "10", "decimal"));// kg
        defaultSettings.add(new ServerSetting("tokenExpirationDelay", "604800", "integer"));// second


        for (ServerSetting i : defaultSettings) {
            Optional<ServerSetting> setting = serverSettingsRepository.findById(i.getId());

            if (setting.isEmpty()) {
                savedSettingsCount++;
                serverSettingsRepository.save(i);
            }
        }

        Optional<School> school = schoolRepository.findById(0L);

        if(school.isEmpty()){
            School newSchool = new School();
            newSchool.setEname("Other");
            newSchool.setName("其他学院");
            newSchool.setId(0L);
            schoolRepository.save(newSchool);
        }


        Optional<User> admin = userRepository.findByUsername("admin");

        if (admin.isEmpty()) {
            User newAdmin = new User();
            newAdmin.setUsername("admin");
            newAdmin.setPassword(passwordEncoder.encode("password"));
            newAdmin.setRoles(Arrays.asList("ROLE_USER", "ROLE_ADMIN"));
            newAdmin.setSchool(school.get());
            userRepository.save(newAdmin);
        }


        int finalSavedSettingsCount = savedSettingsCount;
        return args -> {
            LOGGER.info("Added " + finalSavedSettingsCount + " default settings.");
        };
    }
}
