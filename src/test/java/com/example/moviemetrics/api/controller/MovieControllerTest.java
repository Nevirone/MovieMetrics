package com.example.moviemetrics.api.controller;

import com.example.moviemetrics.MovieMetricsApplication;
import com.example.moviemetrics.api.model.ERole;
import com.example.moviemetrics.api.model.Movie;
import com.example.moviemetrics.api.model.User;
import com.example.moviemetrics.api.repository.IGenreRepository;
import com.example.moviemetrics.api.repository.IMovieRepository;
import com.example.moviemetrics.api.repository.IUserRepository;
import com.example.moviemetrics.api.DTO.AuthenticationDto;
import com.example.moviemetrics.api.DTO.MovieDto;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = MovieMetricsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class MovieControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    IGenreRepository genreRepository;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    IMovieRepository movieRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    final TestRestTemplate restTemplate = new TestRestTemplate();
    final HttpHeaders userHeaders = new HttpHeaders();
    final HttpHeaders adminHeaders = new HttpHeaders();

    private String getURL(String uri) {
        return "http://localhost:" + port + uri;
    }

    private MovieDto createMovieDto(String title) {
        List<String> genres = Arrays.asList("Drama", "Comedy");
        return MovieDto
                .builder()
                .title(title)
                .description("A mind-bending heist movie")
                .popularity(9.2)
                .voteAverage(8.7)
                .voteCount(1500)
                .genres(new HashSet<>(genres))
                .build();
    }
    private Movie createMovie(String title) {
        return movieRepository.save(
                Movie.builder()
                        .title(title)
                        .description("test")
                        .voteAverage(9.2)
                        .voteCount(120)
                        .voteAverage(7.5)
                        .genres(new HashSet<>())
                        .build()
        );
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
        movieRepository.deleteAll();
        genreRepository.deleteAll();
    }

    @Test
    @DisplayName("Create Movie: Successful")
    public void testPostMovie() {
        // given
        MovieDto movieDto = createMovieDto("Inception");

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(201, response.getStatusCode().value());
    }


    @Test
    @DisplayName("Create Movie: Duplicate Title Conflict")
    public void testPostMovieTakenTitle() {
        // given
        MovieDto movieDto = createMovieDto("Inception");

        createMovie(movieDto.getTitle());

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Create Movie: Invalid Title Format")
    public void testPostMovieBadTitle() {
        // given
        MovieDto movieDto = createMovieDto("Ux");

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies"),
                HttpMethod.POST, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Update Movie: Successful")
    public void testPatchMovie() {
        // given
        MovieDto movieDto = createMovieDto("Inception");

        Movie saved = createMovie("Saw");

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Update Movie: No permission")
    public void testPatchMovieNoPermission() {
        // given
        MovieDto movieDto = createMovieDto("Inception");

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/1"),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Update Movie: Duplicate Title Conflict")
    public void testPatchMovieTakenTitle() {
        // given
        MovieDto movieDto = createMovieDto("Inception");

        createMovie(movieDto.getTitle());

        Movie saved = createMovie("Saw");

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + saved.getId()),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Update Movie: Not Found")
    public void testPatchMovieNotFound() {
        // given
        MovieDto movieDto = createMovieDto("Inception");

        HttpEntity<MovieDto> entity = new HttpEntity<>(movieDto, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/12"),
                HttpMethod.PATCH, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Get Movie: Successful")
    public void testGetMovieById() {
        // given
        Movie saved = createMovie("Saw");

        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + saved.getId()),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Get Movie: Not Found")
    public void testGetMovieByIdWrongId() {
        // given
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/12"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Delete Movie: Successful")
    public void testDeleteMovie() {
        // given
        Movie saved = createMovie("Saw");

        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/" + saved.getId()),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Delete Movie: No permission")
    public void testDeleteMovieNoPermission() {
        // given
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/1"),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Delete Movie: Not Found")
    public void testDeleteMovieWithById() {
        // given
        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies/12"),
                HttpMethod.DELETE, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Get All Movies: Successful")
    public void testGetMovies() {
        // given
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/api/movies"),
                HttpMethod.GET, entity, String.class);

        // then
        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }
}
