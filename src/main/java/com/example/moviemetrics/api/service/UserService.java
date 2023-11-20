package com.example.moviemetrics.api.service;
import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.example.moviemetrics.api.model.User;

import com.example.moviemetrics.api.repository.IUserRepository;

@Service
public class UserService {
    private final IUserRepository userRepository;

    @Autowired
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User newUser) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
            throw new DataConflictException("User email taken");
        }

        return userRepository.save(newUser);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(NotFoundException::new);
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User user) {
        if(userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("User not found");
        }

        Optional<User> emailExists = userRepository.findByEmail(user.getEmail());
        if (emailExists.isPresent() && !Objects.equals(emailExists.get().getId(), id)) {
            throw new DataConflictException("User email taken");
        }

        return userRepository.save(user);
    }

    @Transactional
    public User deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        userRepository.deleteById(id);
        return user.get();
    }
}
