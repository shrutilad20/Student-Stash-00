package com.studentstash.studentstash.service;

import com.studentstash.studentstash.dto.AuthResponse;
import com.studentstash.studentstash.dto.LoginRequest;
import com.studentstash.studentstash.dto.RegisterRequest;
import com.studentstash.studentstash.entity.User;
import com.studentstash.studentstash.repository.UserRepository;
import com.studentstash.studentstash.security.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;

    // REGISTER USER
    public String register(RegisterRequest request) {

        // CHECK EMAIL EXISTS
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // CREATE USER
        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        // TEMP PASSWORD WITHOUT ENCRYPTION
        user.setPassword(request.getPassword());

        user.setCollege(request.getCollege());
        user.setBranch(request.getBranch());
        user.setSemester(request.getSemester());
        user.setRole(request.getRole());

        // SAVE USER
        userRepository.save(user);

        return "User Registered Successfully";
    }

    // LOGIN USER
    public AuthResponse login(LoginRequest request) {

        // FIND USER
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        // CHECK PASSWORD
        if (!request.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid Password");
        }

        // GENERATE JWT TOKEN
        String token = jwtUtil.generateToken(user.getEmail());

        // RETURN RESPONSE
        return new AuthResponse(
                token,
                "Login Successful"
        );
    }
}