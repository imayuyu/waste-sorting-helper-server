package com.charliechiang.wastesortinghelperserver.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secretKey = "QNu7xWLK7jrhHKPrY5zCA5MFKSfAvJn";

    public JwtProperties() {

    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
