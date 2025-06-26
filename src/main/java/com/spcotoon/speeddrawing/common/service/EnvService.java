package com.spcotoon.speeddrawing.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EnvService {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    public boolean isProd() {
        return "prod".equalsIgnoreCase(activeProfile);
    }
}
