package com.example.moviemetrics.api.controller;

import com.example.moviemetrics.MovieMetricsApplication;
import com.example.moviemetrics.api.DTO.AuthenticationDto;
import com.example.moviemetrics.api.DTO.GenreDto;
import com.example.moviemetrics.api.model.ERole;
import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.model.User;
import com.example.moviemetrics.api.repository.IGenreRepository;
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

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MovieMetricsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class GenreControllerTest {
    final TestRestTemplate restTemplate = new TestRestTemplate();
    final HttpHeaders userHeaders = new HttpHeaders();
    final HttpHeaders adminHeaders = new HttpHeaders();
    @Autowired
    IGenreRepository genreRepository;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @LocalServerPort
    private int port;

    private String getURL(String uri) {
        return "http://localhost:" + port + uri;
    }

    private Genre createGenre(String name) {
        return genreRepository.save(
                Genre.builder().name(name).build()
        );
    }

    private GenreDto createGenreDto(String name) {
        return GenreDto.builder().name(name).build();
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

    @BeforeEach
    public void cleanUp() {
        genreRepository.deleteAll();
    }

    @Test
    @DisplayName("Create Genre: Successful")
    public void testPostGenre() {
        // given
        GenreDto genreDto = createGenreDto("Action");

        HttpEntity<GenreDto> entity = new HttpEntity<>(genreDto, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/genres"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(201, response.getStatusCode().value());
    }


    @Test
    @DisplayName("Create Genre: Duplicate Title Conflict")
    public void testPostGenreTakenTitle() {
        // given
        GenreDto genreDto = createGenreDto("Action");

        createGenre("Action");

        HttpEntity<GenreDto> entity = new HttpEntity<>(genreDto, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/genres"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Create Genre: Invalid Title Format")
    public void testPostGenreBadTitle() {
        // given
        GenreDto genreDto = createGenreDto("a");

        HttpEntity<GenreDto> entity = new HttpEntity<>(genreDto, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/genres"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Update Genre: Successful")
    public void testPatchGenre() {
        // given
        GenreDto genreDto = createGenreDto("Action");

        Genre saved = createGenre("Drama");

        HttpEntity<GenreDto> entity = new HttpEntity<>(genreDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/genres/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Update Genre: No permission")
    public void testPatchGenreNoPermission() {
        // given
        GenreDto genreDto = createGenreDto("Action");

        HttpEntity<GenreDto> entity = new HttpEntity<>(genreDto, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/genres/1"),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Update Genre: Duplicate Title Conflict")
    public void testPatchGenreTakenTitle() {
        // given
        GenreDto genreDto = createGenreDto("Action");

        createGenre("Action");

        Genre saved = createGenre("Drama");

        HttpEntity<GenreDto> entity = new HttpEntity<>(genreDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/genres/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Update Genre: Not Found")
    public void testPatchGenreNotFound() {
        // given
        GenreDto genreDto = createGenreDto("Action");

        HttpEntity<GenreDto> entity = new HttpEntity<>(genreDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/genres/12"),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Get Genre: Successful")
    public void testGetGenreById() {
        // given
        Genre saved = createGenre("Action");

        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/genres/" + saved.getId()),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Get Genre: Not Found")
    public void testGetGenreByIdWrongId() {
        // given
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/genres/12"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Delete Genre: Successful")
    public void testDeleteGenre() {
        // given
        Genre saved = createGenre("Action");

        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/genres/" + saved.getId()),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Delete Genre: No permission")
    public void testDeleteGenreNoPermission() {
        // given
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/genres/1"),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Delete Genre: Not Found")
    public void testDeleteGenreWithById() {
        // given
        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/genres/12"),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Get All Genres: Successful")
    public void testGetGenres() {
        // given
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/genres"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }
}
