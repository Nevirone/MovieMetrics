package com.example.moviemetrics.api.controller;

import com.example.moviemetrics.api.exception.NotFoundException;
import com.example.moviemetrics.api.model.User;
import com.example.moviemetrics.api.request.LoginRequest;
import com.example.moviemetrics.api.request.RegisterRequest;
import com.example.moviemetrics.api.util.JWTProvider;
import com.example.moviemetrics.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JWTProvider jwtProvider;

    @Autowired
    public AuthController(UserService userService, JWTProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    private boolean validPassword(String password, String hashedPassword) {
        final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(password, hashedPassword);
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.getUserByEmail(loginRequest.getEmail());

            if (!validPassword(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email or password incorrect");
            }

            return ResponseEntity.status(HttpStatus.OK).body(JWTProvider.generateToken(user));

        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email or password incorrect");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = registerRequest.getUser();

        User createdUser = userService.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(JWTProvider.generateToken(createdUser));
    }


}
