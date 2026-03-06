package com.auth_service.auth.controllers;

import com.auth_service.auth.dtos.AuthenticationRequest;
import com.auth_service.auth.entities.User;
import com.auth_service.auth.services.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
            throws Exception {
        return ResponseEntity.ok(authService.createAuthenticationToken(authenticationRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthenticationRequest authenticationRequest) {
        User registeredUser = authService.register(authenticationRequest);
        return ResponseEntity.ok(registeredUser);
    }

    @GetMapping("/doctor")
    public ResponseEntity<?> doctorEndpoint() {
        return ResponseEntity.ok("Welcome, Doctor!");
    }

    @GetMapping("/patient")
    public ResponseEntity<?> patientEndpoint() {
        return ResponseEntity.ok("Welcome, Patient!");
    }
}