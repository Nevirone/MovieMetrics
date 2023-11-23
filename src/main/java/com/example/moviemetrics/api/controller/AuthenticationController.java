package com.example.moviemetrics.api.controller;

import com.example.moviemetrics.api.service.JwtService;
import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import com.example.moviemetrics.api.model.User;
import com.example.moviemetrics.api.DTO.AuthenticationDto;
import com.example.moviemetrics.api.DTO.RegistrationDto;
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

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody AuthenticationDto authenticationDto) {
        try {
            User user = authenticationService.authenticate(authenticationDto);
            String token = jwtService.generateToken(user);

            return ResponseEntity.status(HttpStatus.OK).body(token);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email or password incorrect");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationDto registrationDto) {
        try {
            User user = authenticationService.register(registrationDto);
            String token = jwtService.generateToken(user);

            return ResponseEntity.status(HttpStatus.OK).body(token);
        } catch (DataConflictException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email taken");
        }
    }


}
