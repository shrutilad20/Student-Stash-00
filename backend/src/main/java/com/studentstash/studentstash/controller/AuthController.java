package com.studentstash.studentstash.controller;

import com.studentstash.studentstash.dto.AuthResponse;
import com.studentstash.studentstash.dto.LoginRequest;
import com.studentstash.studentstash.dto.RegisterRequest;

import com.studentstash.studentstash.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")

@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // REGISTER API
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request) {

        return ResponseEntity.ok(
                authService.register(request)
        );
    }

    // LOGIN API
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }
}