package com.charliechiang.wastesortinghelperserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WasteSortingHelperServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WasteSortingHelperServerApplication.class, args);
    }

}
