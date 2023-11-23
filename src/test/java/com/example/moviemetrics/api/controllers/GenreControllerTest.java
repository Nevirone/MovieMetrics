package com.example.moviemetrics.api.controllers;

import com.example.moviemetrics.MovieMetricsApplication;
import com.example.moviemetrics.api.model.ERole;
import com.example.moviemetrics.api.model.User;
import com.example.moviemetrics.api.repository.IUserRepository;
import com.example.moviemetrics.api.DTO.AuthenticationDto;
import com.example.moviemetrics.api.DTO.GenreDto;
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

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = MovieMetricsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class GenreControllerTest {
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

    @BeforeAll
        public void createUserAndLogIn() throws Exception {
            String email = "test@test.com";
        String password = "TestPassword";

        String adminEmail = "admin@test.com";
        String adminPassword = "AdminPassword";

        User user = User
                .builder()
                .email(email)
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
                .email(email)
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

    @Test
    @Order(1)
    @DisplayName("Create Genre: Successful")
    public void testPostGenre() {
        GenreDto genreDto = GenreDto
                .builder()
                .name("Action")
                .build();

        HttpEntity<GenreDto> entity = new HttpEntity<>(genreDto, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres"),
                HttpMethod.POST, entity, String.class);

        genreDto.setName("Drama");
        entity = new HttpEntity<>(genreDto, userHeaders);

        restTemplate.exchange(
                getURL("/genres"),
                HttpMethod.POST, entity, String.class);

        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(201, response.getStatusCode().value());
    }


    @Test
    @Order(2)
    @DisplayName("Create Genre: Duplicate Name Conflict")
    public void testPostGenreTakenName() {
        GenreDto genreDto = GenreDto
                .builder()
                .name("Action")
                .build();

        HttpEntity<GenreDto> entity = new HttpEntity<>(genreDto, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres"),
                HttpMethod.POST, entity, String.class);

        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    @Order(3)
    @DisplayName("Create Genre: Invalid Name Format")
    public void testPostGenreBadName() {
        GenreDto genreDto = GenreDto
                .builder()
                .name("xx")
                .build();

        HttpEntity<GenreDto> entity = new HttpEntity<>(genreDto, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres"),
                HttpMethod.POST, entity, String.class);

        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    @Order(4)
    @DisplayName("Update Genre: Successful")
    public void testPatchGenre() {
        GenreDto genreDto = GenreDto
                .builder()
                .name("Comedy")
                .build();

        HttpEntity<GenreDto> entity = new HttpEntity<>(genreDto, adminHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres/1"),
                HttpMethod.PATCH, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @Order(5)
    @DisplayName("Update Genre: No permission")
    public void testPatchGenreNoPermission() {
        GenreDto genreDto = GenreDto
                .builder()
                .name("Comedy")
                .build();

        HttpEntity<GenreDto> entity = new HttpEntity<>(genreDto, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres/1"),
                HttpMethod.PATCH, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    @Order(6)
    @DisplayName("Update Genre: Duplicate Name Conflict")
    public void testPatchGenreTakenName() {
        GenreDto genreDto = GenreDto
                .builder()
                .name("Comedy")
                .build();

        HttpEntity<GenreDto> entity = new HttpEntity<>(genreDto, adminHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres/2"),
                HttpMethod.PATCH, entity, String.class);

        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    @Order(7)
    @DisplayName("Update Genre: Not Found")
    public void testPatchGenreWrongId() {
        GenreDto genreDto = GenreDto
                .builder()
                .name("Thriller")
                .build();

        HttpEntity<GenreDto> entity = new HttpEntity<>(genreDto, adminHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres/12"),
                HttpMethod.PATCH, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @Order(8)
    @DisplayName("Get Genre: Successful")
    public void testGetGenreById() {
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres/1"),
                HttpMethod.GET, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @Order(9)
    @DisplayName("Get Genre: Not Found")
    public void testGetGenreByIdWrongId() {
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres/12"),
                HttpMethod.GET, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @Order(10)
    @DisplayName("Delete Genre: Successful")
    public void testDeleteGenres() {
        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres/1"),
                HttpMethod.DELETE, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @Order(11)
    @DisplayName("Delete Genre: No permission")
    public void testDeleteGenresNoPermission() {
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres/1"),
                HttpMethod.DELETE, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    @Order(12)
    @DisplayName("Delete Genre: Not Found")
    public void testDeleteGenresWithById() {
        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres/12"),
                HttpMethod.DELETE, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Get All Genres: Successful")
    public void testGetGenres() {
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres"),
                HttpMethod.GET, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }
}
