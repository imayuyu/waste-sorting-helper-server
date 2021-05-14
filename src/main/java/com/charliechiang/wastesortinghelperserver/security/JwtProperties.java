package com.charliechiang.wastesortinghelperserver.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secretKey = "QNu7xWLK7jrhHKPrY5zCA5MFKSfAvJn";
    //                          h    m    s   ms
    private long validityInMs = 1 * 60 * 60 * 1000;

    public JwtProperties() {

    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getValidityInMs() {
        return validityInMs;
    }

    public void setValidityInMs(long validityInMs) {
        this.validityInMs = validityInMs;
    }
}
