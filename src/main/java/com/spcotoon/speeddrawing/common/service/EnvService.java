package com.spcotoon.speeddrawing.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EnvService {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    public boolean isProd() {
        if (activeProfile == null || activeProfile.isEmpty()) {
            return false;
        }

        return "prod".equalsIgnoreCase(activeProfile);
    }
}
