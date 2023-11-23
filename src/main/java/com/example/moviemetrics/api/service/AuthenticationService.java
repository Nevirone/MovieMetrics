package com.example.moviemetrics.api.service;

import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import com.example.moviemetrics.api.model.ERole;
import com.example.moviemetrics.api.model.User;
import com.example.moviemetrics.api.repository.IUserRepository;
import com.example.moviemetrics.api.DTO.AuthenticationDto;
import com.example.moviemetrics.api.DTO.RegistrationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public User register(RegistrationDto registrationDto) {
        if(userRepository.findByEmail(registrationDto.getEmail()).isPresent())
            throw new DataConflictException("User email taken");

        User user = User
                .builder()
                .email(registrationDto.getEmail())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .ERole(ERole.USER)
                .build();

        return userRepository.save(user);
    }

    public User authenticate(AuthenticationDto authenticationDto) {
        Optional<User> user = userRepository.findByEmail(authenticationDto.getEmail());

        if(user.isEmpty())
            throw new NotFoundException("User not found");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationDto.getEmail(),
                        authenticationDto.getPassword()));


        return user.get();
    }
}
