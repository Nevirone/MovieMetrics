package com.example.moviemetrics.api.controller;

import com.example.moviemetrics.MovieMetricsApplication;
import com.example.moviemetrics.api.DTO.AuthenticationDto;
import com.example.moviemetrics.api.DTO.RegistrationDto;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MovieMetricsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AuthenticationControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    IUserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    final TestRestTemplate restTemplate = new TestRestTemplate();

    private String getURL(String uri) {
        return "http://localhost:" + port + uri;
    }

    private User createUser(String email, String password) {
        return userRepository.save(
                User.builder()
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .ERole(ERole.USER)
                        .build()
        );
    }

    private AuthenticationDto createAuthenticationDto(String email, String password) {
        return AuthenticationDto.builder()
                .email(email)
                .password(password)
                .build();
    }

    private RegistrationDto createRegistrationDto(String email, String password) {
        return RegistrationDto.builder()
                .email(email)
                .password(password)
                .build();
    }
    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Register User: Successful")
    public void testRegister() {
        // given
        HttpHeaders headers = new HttpHeaders();
        RegistrationDto registrationDto = createRegistrationDto("test@test.com", "testpass");


        HttpEntity<RegistrationDto> entity = new HttpEntity<>(registrationDto, headers);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/auth/register"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Register User: Bad Email")
    public void testRegisterBadEmail() {
        // given
        HttpHeaders headers = new HttpHeaders();
        RegistrationDto registrationDto = createRegistrationDto("test", "testpass");


        HttpEntity<RegistrationDto> entity = new HttpEntity<>(registrationDto, headers);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/auth/register"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().toLowerCase().contains("email"));
    }

    @Test
    @DisplayName("Register User: Bad Password")
    public void testRegisterBadPassword() {
        // given
        HttpHeaders headers = new HttpHeaders();
        RegistrationDto registrationDto = createRegistrationDto("test@test.com", "tes");


        HttpEntity<RegistrationDto> entity = new HttpEntity<>(registrationDto, headers);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/auth/register"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().toLowerCase().contains("password"));
    }

    @Test
    @DisplayName("Register User: Email taken")
    public void testRegisterEmailTaken() {
        // given
        HttpHeaders headers = new HttpHeaders();
        RegistrationDto registrationDto = createRegistrationDto("test@test.com", "testpass");

        createUser(registrationDto.getEmail(), "testpass");


        HttpEntity<RegistrationDto> entity = new HttpEntity<>(registrationDto, headers);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/auth/register"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(409, response.getStatusCode().value());
        assertTrue(response.getBody().toLowerCase().contains("email"));
    }

    @Test
    @DisplayName("Login User: Successful")
    public void testLogin() {
        // given
        HttpHeaders headers = new HttpHeaders();
        AuthenticationDto authenticationDto = createAuthenticationDto("test@test.com", "testpass");

        createUser(authenticationDto.getEmail(), authenticationDto.getPassword());

        HttpEntity<AuthenticationDto> entity = new HttpEntity<>(authenticationDto, headers);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/auth/login"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Login User: Not Found")
    public void testLoginNotFound() {
        // given
        HttpHeaders headers = new HttpHeaders();
        AuthenticationDto authenticationDto = createAuthenticationDto("test@test.com", "testpass");

        HttpEntity<AuthenticationDto> entity = new HttpEntity<>(authenticationDto, headers);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/auth/login"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Login User: Bad Email")
    public void testLoginBadEmail() {
        // given
        HttpHeaders headers = new HttpHeaders();
        AuthenticationDto authenticationDto = createAuthenticationDto("test", "testpass");

        HttpEntity<AuthenticationDto> entity = new HttpEntity<>(authenticationDto, headers);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/auth/login"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().toLowerCase().contains("email"));
    }
}
