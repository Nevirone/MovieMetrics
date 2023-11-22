package com.example.moviemetrics.api.controller;

import com.example.moviemetrics.api.service.JwtService;
import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import com.example.moviemetrics.api.model.User;
import com.example.moviemetrics.api.request.AuthenticationRequest;
import com.example.moviemetrics.api.request.RegisterRequest;
import com.example.moviemetrics.api.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<?> test() {
        System.out.println("asd");
        return ResponseEntity.ok().build();
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        try {
            User user = authenticationService.authenticate(authenticationRequest);
            String token = jwtService.generateToken(user);

            return ResponseEntity.status(HttpStatus.OK).body(token);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email or password incorrect");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = authenticationService.register(registerRequest);
            String token = jwtService.generateToken(user);

            return ResponseEntity.status(HttpStatus.OK).body(token);
        } catch (DataConflictException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email taken");
        }
    }


}
