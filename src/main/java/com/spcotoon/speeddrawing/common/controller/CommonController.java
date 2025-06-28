package com.spcotoon.speeddrawing.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {

    @GetMapping("/health")
    public ResponseEntity<?> heathCheck() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test/jwt-filter")
    public ResponseEntity<?> jwtFilterCheck() {
        return ResponseEntity.ok().build();
    }
}
