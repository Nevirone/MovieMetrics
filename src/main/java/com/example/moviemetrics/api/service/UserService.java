package com.example.moviemetrics.api.service;
import com.example.moviemetrics.api.DTO.MovieDto;
import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import com.example.moviemetrics.api.model.ERole;
import com.example.moviemetrics.api.DTO.UserDto;
import com.example.moviemetrics.api.model.Movie;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.example.moviemetrics.api.model.User;

import com.example.moviemetrics.api.repository.IUserRepository;

@Service
public class UserService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserDto userDto) {
        if(userRepository.findByEmail(userDto.getEmail()).isPresent())
            throw new DataConflictException("User email taken");

        User user = User
                .builder()
                .email(userDto.getEmail())
                .password(userDto.isPasswordEncrypted()? userDto.getPassword() : passwordEncoder.encode(userDto.getPassword()))
                .ERole(userDto.isAdmin()? ERole.ADMIN : ERole.USER)
                .build();

        return userRepository.save(user);
    }

    public List<User> createUsers(List<UserDto> userDtoList) throws DataConflictException {
        List<User> users = new ArrayList<>();

        System.out.println("Loading users:");
        for(UserDto userDto : userDtoList)
            try {
                users.add(createUser(userDto));
            } catch (DataConflictException ex) {
                System.out.println("Email exists: " + userDto.getEmail());
            }

        return users;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, UserDto userDto) {
        Optional<User> userFound = userRepository.findById(id);
        if(userFound.isEmpty())
            throw new NotFoundException("User not found");

        Optional<User> emailExists = userRepository.findByEmail(userDto.getEmail());

        if (emailExists.isPresent() && !Objects.equals(emailExists.get().getId(), id))
            throw new DataConflictException("User email taken");

        User user = User
                .builder()
                .id(userFound.get().getId())
                .email(userDto.getEmail())
                .password(userDto.isPasswordEncrypted()? userDto.getPassword() : passwordEncoder.encode(userDto.getPassword()))
                .ERole(userDto.isAdmin()? ERole.ADMIN : ERole.USER)
                .build();

        return userRepository.save(user);
    }

    @Transactional
    public User deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty())
            throw new NotFoundException("User not found");

        userRepository.deleteById(id);
        return user.get();
    }
}
