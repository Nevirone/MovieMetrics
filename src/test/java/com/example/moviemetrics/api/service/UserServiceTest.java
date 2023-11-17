package com.example.moviemetrics.api.service;

import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import com.example.moviemetrics.api.model.User;
import com.example.moviemetrics.api.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ComponentScan(basePackages = "com.example.moviemetrics")
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private IUserRepository iUserRepository;

    @BeforeEach
    void beforeEach() {
        iUserRepository.deleteAll();
    }

    @Test
    @DisplayName("Create user should be created successfully")
    public void testCreateUser() {
        // given
        User user = new User("test@example.com", "password", "TestUser");

        // when
        List<User> users = userService.getAllUsers();

        // then
        User result = assertDoesNotThrow(() -> userService.createUser(user));
        List<User> usersAfterCreate = userService.getAllUsers();

        assertInstanceOf(User.class, result);
        assertEquals(users.size() + 1, usersAfterCreate.size());
        assertEquals(user, result);
    }

    @Test
    @DisplayName("Create user when email is taken")
    public void testCreateUserWhenEmailTaken() {
        // given
        User user = new User("test@example.com", "password", "TestUser");

        userService.createUser(user);

        // when
        List<User> users = userService.getAllUsers();

        // then
        assertThrows(DataConflictException.class, () -> userService.createUser(user));
        List<User> usersAfterCreate = userService.getAllUsers();

        assertEquals(users.size(), usersAfterCreate.size());
    }

    @Test
    @DisplayName("Get user by email when user exists")
    public void testGetUserByEmailWhenUserExists() {
        // given
        String email = "test@example.com";
        User user = new User(email, "password", "TestUser");

        userService.createUser(user);

        // when
        User u = userService.getUserByEmail(email);

        // then
        assertNotNull(u);
        assertInstanceOf(User.class, u);
    }

    @Test
    @DisplayName("Get user by id when user does not exist")
    public void testGetUserByIdWhenUserDoesNotExist() {
        // given
        Long id = 10L;

        // then
        assertThrows(NotFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    @DisplayName("Get user by email when user does not exist")
    public void testGetUserByEmailWhenUserDoesNotExist() {
        // given
        String email = "nonexistent@example.com";

        // then
        assertThrows(NotFoundException.class, () -> userService.getUserByEmail(email));
    }

    @Test
    @DisplayName("Get all users when users exist")
    public void testGetAllUsersWhenUsersExist() {
        // given
        User user = new User("test@example.com", "password", "TestUser");

        userService.createUser(user);

        // when
        List<User> users = userService.getAllUsers();

        // then
        assertInstanceOf(List.class, users);
        assertFalse(users.isEmpty());
    }

    @Test
    @DisplayName("Get all users when no users exist")
    public void testGetAllUsersWhenNoUsersExist() {
        // when
        List<User> users = userService.getAllUsers();

        // then
        assertInstanceOf(List.class, users);
        assertTrue(users.isEmpty());
    }

    @Test
    @DisplayName("Delete user when user exists")
    public void testDeleteUserWhenExists() {
        // given
        User user = new User("test@example.com", "password", "TestUser");

        userService.createUser(user);

        // when
        List<User> users = userService.getAllUsers();
        User target = users.get(0);

        // then
        User result = assertDoesNotThrow(() -> userService.deleteUser(target.getId()));
        assertInstanceOf(User.class, result);
        assertEquals(target, result);
    }

    @Test
    @DisplayName("Delete user when user does not exist")
    public void testDeleteUserWhenDoesNotExist() {
        // then
        assertThrows(NotFoundException.class, () -> userService.deleteUser(10L));
    }

    @Test
    @DisplayName("Update user when user exists")
    public void testUpdateUserWhenExists() {
        // given
        String newEmail = "updated@example.com";
        User user = new User("test@example.com", "password", "TestUser");

        userService.createUser(user);

        // when
        user.setEmail(newEmail);

        // then
        User result = assertDoesNotThrow(() -> userService.updateUser(user.getId(), user));
        assertInstanceOf(User.class, result);
        assertEquals(newEmail, result.getEmail());
    }

    @Test
    @DisplayName("Update user when user does not exist")
    public void testUpdateUserWhenDoesNotExist() {
        // then
        assertThrows(NotFoundException.class, () -> userService.updateUser(10L, null));
    }
}
