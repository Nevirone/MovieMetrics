package com.example.moviemetrics.api.controllers;

import com.example.moviemetrics.MovieMetricsApplication;
import com.example.moviemetrics.api.model.ERole;
import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.model.User;
import com.example.moviemetrics.api.repository.IGenreRepository;
import com.example.moviemetrics.api.repository.IUserRepository;
import com.example.moviemetrics.api.request.AuthenticationRequest;
import com.example.moviemetrics.api.request.MovieRequest;
import com.example.moviemetrics.api.request.RegisterRequest;
import com.example.moviemetrics.api.request.UserRequest;
import com.example.moviemetrics.api.service.UserService;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    PasswordEncoder passwordEncoder;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders userHeaders = new HttpHeaders();
    HttpHeaders adminHeaders = new HttpHeaders();

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

        AuthenticationRequest authenticationRequest = AuthenticationRequest
                .builder()
                .email(email)
                .password(password)
                .build();

        HttpEntity<AuthenticationRequest> authenticationEntity = new HttpEntity<>(authenticationRequest, userHeaders);

        ResponseEntity<String> authenticationResponse = restTemplate.exchange(
                getURL("/auth/login"),
                HttpMethod.POST, authenticationEntity, String.class);

        if(authenticationResponse.getStatusCode().value() != 200) throw new Exception("User failed to be logged in");
        else userHeaders.setBearerAuth(Objects.requireNonNull(authenticationResponse.getBody()));

        authenticationRequest = AuthenticationRequest
                .builder()
                .email(adminEmail)
                .password(adminPassword)
                .build();

        authenticationEntity = new HttpEntity<>(authenticationRequest, adminHeaders);

        authenticationResponse = restTemplate.exchange(
                getURL("/auth/login"),
                HttpMethod.POST, authenticationEntity, String.class);

        if(authenticationResponse.getStatusCode().value() != 200) throw new Exception("User failed to be logged in");
        else adminHeaders.setBearerAuth(Objects.requireNonNull(authenticationResponse.getBody()));

        genreRepository.save(Genre.builder().name("Action").build());
        genreRepository.save(Genre.builder().name("Drama").build());
        genreRepository.save(Genre.builder().name("Comedy").build());
        genreRepository.save(Genre.builder().name("Sci-Fi").build());
    }

    @Test
    @Order(1)
    @DisplayName("Create Movie: Successful")
    public void testPostMovie() {
        List<Long> genres = Arrays.asList(4L, 1L);
        MovieRequest movieRequest = MovieRequest
                .builder()
                .title("Inception")
                .description("A mind-bending heist movie")
                .popularity(9.2)
                .voteAverage(8.7)
                .voteCount(1500)
                .genreIds(new HashSet<>(genres))
                .build();

        HttpEntity<MovieRequest> entity = new HttpEntity<>(movieRequest, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies"),
                HttpMethod.POST, entity, String.class);


        genres = Arrays.asList(4L, 1L);
        movieRequest = MovieRequest
                .builder()
                .title("The Shawshank Redemption")
                .description("Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.")
                .popularity(9.3)
                .voteAverage(9.2)
                .voteCount(2000)
                .genreIds(new HashSet<>(genres))
                .build();

        entity = new HttpEntity<>(movieRequest, userHeaders);

        restTemplate.exchange(
                getURL("/movies"),
                HttpMethod.POST, entity, String.class);

        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(201, response.getStatusCode().value());
    }


    @Test
    @Order(2)
    @DisplayName("Create Movie: Duplicate Title Conflict")
    public void testPostMovieTakenTitle() {
        List<Long> genres = Arrays.asList(4L, 1L);
        MovieRequest movieRequest = MovieRequest
                .builder()
                .title("Inception")
                .description("A mind-bending heist movie")
                .popularity(9.2)
                .voteAverage(8.7)
                .voteCount(1500)
                .genreIds(new HashSet<>(genres))
                .build();

        HttpEntity<MovieRequest> entity = new HttpEntity<>(movieRequest, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies"),
                HttpMethod.POST, entity, String.class);

        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    @Order(3)
    @DisplayName("Create Movie: Invalid Title Format")
    public void testPostMovieBadTitle() {
        List<Long> genres = Arrays.asList(4L, 1L);
        MovieRequest movieRequest = MovieRequest
                .builder()
                .title("Ux")
                .description("A mind-bending heist movie")
                .popularity(9.2)
                .voteAverage(8.7)
                .voteCount(1500)
                .genreIds(new HashSet<>(genres))
                .build();

        HttpEntity<MovieRequest> entity = new HttpEntity<>(movieRequest, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies"),
                HttpMethod.POST, entity, String.class);

        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    @Order(4)
    @DisplayName("Update Movie: Successful")
    public void testPatchMovie() {
        List<Long> genres = Arrays.asList(4L, 1L);
        MovieRequest movieRequest = MovieRequest
                .builder()
                .title("Inception")
                .description("A mind-bending heist movie")
                .popularity(9.2)
                .voteAverage(8.7)
                .voteCount(1500)
                .genreIds(new HashSet<>(genres))
                .build();

        HttpEntity<MovieRequest> entity = new HttpEntity<>(movieRequest, adminHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies/1"),
                HttpMethod.PATCH, entity, String.class);

        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @Order(5)
    @DisplayName("Update Movie: No permission")
    public void testPatchMovieNoPermission() {
        List<Long> genres = Arrays.asList(4L, 1L);
        MovieRequest movieRequest = MovieRequest
                .builder()
                .title("Inception")
                .description("A mind-bending heist movie")
                .popularity(9.2)
                .voteAverage(8.7)
                .voteCount(1500)
                .genreIds(new HashSet<>(genres))
                .build();

        HttpEntity<MovieRequest> entity = new HttpEntity<>(movieRequest, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies/1"),
                HttpMethod.PATCH, entity, String.class);

        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    @Order(6)
    @DisplayName("Update Movie: Duplicate Title Conflict")
    public void testPatchMovieTakenTitle() {
        List<Long> genres = Arrays.asList(4L, 1L);
        MovieRequest movieRequest = MovieRequest
                .builder()
                .title("The Shawshank Redemption")
                .description("A mind-bending heist movie")
                .popularity(9.2)
                .voteAverage(8.7)
                .voteCount(1500)
                .genreIds(new HashSet<>(genres))
                .build();

        HttpEntity<MovieRequest> entity = new HttpEntity<>(movieRequest, adminHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies/1"),
                HttpMethod.PATCH, entity, String.class);

        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    @Order(7)
    @DisplayName("Update Movie: Not Found")
    public void testPatchMovieNotFound() {
        List<Long> genres = Arrays.asList(4L, 1L);
        MovieRequest movieRequest = MovieRequest
                .builder()
                .title("The Skunk Redemption")
                .description("A mind-bending heist movie")
                .popularity(9.2)
                .voteAverage(8.7)
                .voteCount(1500)
                .genreIds(new HashSet<>(genres))
                .build();

        HttpEntity<MovieRequest> entity = new HttpEntity<>(movieRequest, adminHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies/12"),
                HttpMethod.PATCH, entity, String.class);

        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @Order(8)
    @DisplayName("Get Movie: Successful")
    public void testGetMovieById() {
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies/1"),
                HttpMethod.GET, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @Order(9)
    @DisplayName("Get Movie: Not Found")
    public void testGetMovieByIdWrongId() {
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies/12"),
                HttpMethod.GET, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @Order(10)
    @DisplayName("Delete Movie: Successful")
    public void testDeleteMovie() {
        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies/1"),
                HttpMethod.DELETE, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @Order(11)
    @DisplayName("Delete Movie: No permission")
    public void testDeleteMovieNoPermission() {
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies/1"),
                HttpMethod.DELETE, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    @Order(12)
    @DisplayName("Delete Movie: Not Found")
    public void testDeleteMovieWithById() {
        HttpEntity<String> entity = new HttpEntity<>(null, adminHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies/12"),
                HttpMethod.DELETE, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("Get All Movies: Successful")
    public void testGetMovies() {
        HttpEntity<String> entity = new HttpEntity<>(null, userHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies"),
                HttpMethod.GET, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }
}
