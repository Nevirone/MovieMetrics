package com.example.moviemetrics.api.controller;
import com.example.moviemetrics.api.request.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.moviemetrics.api.exception.UserEmailTakenException;
import com.example.moviemetrics.api.exception.UserNotFoundException;

import com.example.moviemetrics.api.model.User;

import com.example.moviemetrics.api.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        try {
            User createdUser = userService.createUser(userRequest.getUser());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (UserEmailTakenException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch(UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        User newUser = userRequest.getUser();
        newUser.setId(id);

        try {
            User updatedUser = userService.updateUser(id, newUser);
            return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (UserEmailTakenException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            User deletedUser = userService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.OK).body(deletedUser);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
