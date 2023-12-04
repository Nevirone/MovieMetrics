package com.example.moviemetrics.api.controller;

import com.example.moviemetrics.MovieMetricsApplication;
import com.example.moviemetrics.api.DTO.AuthenticationDto;
import com.example.moviemetrics.api.DTO.UserDto;
import com.example.moviemetrics.api.model.ERole;
import com.example.moviemetrics.api.model.User;
import com.example.moviemetrics.api.repository.IUserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MovieMetricsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    IUserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    final TestRestTemplate restTemplate = new TestRestTemplate();
    final HttpHeaders userHeaders = new HttpHeaders();
    final HttpHeaders adminHeaders = new HttpHeaders();

    private String getURL(String uri) {
        return "http://localhost:" + port + uri;
    }

    private User createUser(String email) {
        return userRepository.save(
                User.builder()
                        .email(email)
                        .password("testpass")
                        .ERole(ERole.USER)
                        .build()
        );
    }

    private UserDto createUserDto(String email) {
        return UserDto.builder()
                .email(email)
                .password("testpass")
                .isPasswordEncrypted(true)
                .isAdmin(false)
                .build();
    }

    String userEmail = "user@user.com";
    String adminEmail = "admin@admin.com";

    @BeforeAll
    public void createUserAndLogIn() throws Exception {
        String password = "UserPassword";
        String adminPassword = "AdminPassword";

        User user = User
                .builder()
                .email(userEmail)
                .password(passwordEncoder.encode(password))
                .ERole(ERole.USER)
                .build();

        User admin = User
                .builder()
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .ERole(ERole.ADMIN)
                .build();

        userRepository.save(user);
        userRepository.save(admin);

        AuthenticationDto authenticationDto = AuthenticationDto
                .builder()
                .email(userEmail)
                .password(password)
                .build();

        HttpEntity<AuthenticationDto> authenticationEntity = new HttpEntity<>(authenticationDto, userHeaders);

        ResponseEntity<String> authenticationResponse = restTemplate.exchange(
                getURL("/auth/login"),
                HttpMethod.POST, authenticationEntity, String.class);

        if(authenticationResponse.getStatusCode().value() != 200) throw new Exception("User failed to be logged in");
        else userHeaders.setBearerAuth(Objects.requireNonNull(authenticationResponse.getBody()));

        authenticationDto = AuthenticationDto
                .builder()
                .email(adminEmail)
                .password(adminPassword)
                .build();

        authenticationEntity = new HttpEntity<>(authenticationDto, adminHeaders);

        authenticationResponse = restTemplate.exchange(
                getURL("/auth/login"),
                HttpMethod.POST, authenticationEntity, String.class);

        if(authenticationResponse.getStatusCode().value() != 200) throw new Exception("User failed to be logged in");
        else adminHeaders.setBearerAuth(Objects.requireNonNull(authenticationResponse.getBody()));
    }

    @BeforeEach
    void cleanUp() {
        List<User> users = userRepository.findAll();
        for(User user : users) {
            if (!Objects.equals(user.getEmail(), userEmail) && !Objects.equals(user.getEmail(), adminEmail)) {
                userRepository.delete(user);
            }
        }
    }

    @Test
    @DisplayName("Create User: Successful")
    public void testPostUser() {
        // given
        UserDto userDto = createUserDto("test@test.com");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(201, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Create User: No Permission")
    public void testPostUserNoPermission() {
        // given
        UserDto userDto = createUserDto("test@test.com");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(403, response.getStatusCode().value());
    }


    @Test
    @DisplayName("Create User: Duplicate Title Conflict")
    public void testPostUserTakenTitle() {
        // given
        UserDto userDto = createUserDto("test@test.com");

        createUser(userDto.getEmail());

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Create User: Invalid Email Format")
    public void testPostUserBadTitle() {
        // given
        UserDto userDto = createUserDto("as");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Update User: Successful")
    public void testPatchUser() {
        // given
        UserDto userDto = createUserDto("test@test.com");

        User saved = createUser("testme@testme.com");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Update User: No permission")
    public void testPatchUserNoPermission() {
        // given
        UserDto userDto = createUserDto("test@test.com");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/1"),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Update User: Duplicate Title Conflict")
    public void testPatchUserTakenTitle() {
        // given
        UserDto userDto = createUserDto("test@test.com");

        createUser("test@test.com");

        User saved = createUser("testme@testme.com");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Update User: Not Found")
    public void testPatchUserNotFound() {
        // given
        UserDto userDto = createUserDto("test@test.com");

        HttpEntity<UserDto> entity = new HttpEntity<>(userDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/12"),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Get User: Successful")
    public void testGetUserById() {
        // given
        User saved = createUser("test@test.com");

        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Get User: No Permission")
    public void testGetUserByIdNoPermission() {
        // given
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/12"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Get User: Not Found")
    public void testGetUserByIdWrongId() {
        // given
        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/12"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Delete User: Successful")
    public void testDeleteUser() {
        // given
        User saved = createUser("test@test.com");

        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/" + saved.getId()),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Delete User: No permission")
    public void testDeleteUserNoPermission() {
        // given
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/1"),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Delete User: Not Found")
    public void testDeleteUserWithById() {
        // given
        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users/12"),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Get All Users: Successful")
    public void testGetUsers() {
        // given
        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Get All Users: No Permission")
    public void testGetUsersNoPermission() {
        // given
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/users"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(403, response.getStatusCode().value());
    }
}
