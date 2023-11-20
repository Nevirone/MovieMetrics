package com.example.moviemetrics.api.controllers;

import com.example.moviemetrics.MovieMetricsApplication;
import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.model.User;
import com.example.moviemetrics.api.repository.IGenreRepository;
import com.example.moviemetrics.api.repository.IUserRepository;
import com.example.moviemetrics.api.request.LoginRequest;
import com.example.moviemetrics.api.request.MovieRequest;
import com.example.moviemetrics.api.request.RegisterRequest;
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
    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    private String getURL(String uri) {
        return "http://localhost:" + port + uri;
    }


    @BeforeAll
    public void createUserAndLogIn() throws Exception {
        User user = new User("test@test.com", "TestUser", "TestPassword");
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("test@test.com", "TestPassword");

        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/auth/login"),
                HttpMethod.POST, entity, String.class);

        if(response.getStatusCode().value() != 200) throw new Exception("User failed to be logged in");
        else headers.setBearerAuth(Objects.requireNonNull(response.getBody()));

        genreRepository.save(new Genre("Action"));
        genreRepository.save(new Genre("Drama"));
        genreRepository.save(new Genre("Comedy"));
        genreRepository.save(new Genre("Sci-Fi"));
    }

    @Test
    @Order(1)
    @DisplayName("Create Movie: Successful")
    public void testPostMovie() {
        List<Long> genres = Arrays.asList(4L, 1L);
        MovieRequest movieRequest = new MovieRequest(
                "Inception",
                "A mind-bending heist movie",
                9.2,
                8.7,
                1500,
                new HashSet<>(genres));

        HttpEntity<MovieRequest> entity = new HttpEntity<>(movieRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies"),
                HttpMethod.POST, entity, String.class);


        genres = Arrays.asList(4L, 1L);
        movieRequest = new MovieRequest(
                "The Shawshank Redemption",
                "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                9.3,
                9.2,
                2000,
                new HashSet<>(genres));

        entity = new HttpEntity<>(movieRequest, headers);

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
        MovieRequest movieRequest = new MovieRequest(
                "Inception",
                "A mind-bending heist movie",
                9.2,
                8.7,
                1500,
                new HashSet<>(genres));

        HttpEntity<MovieRequest> entity = new HttpEntity<>(movieRequest, headers);

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
        MovieRequest movieRequest = new MovieRequest(
                "Ix",
                "A mind-bending heist movie",
                9.2,
                8.7,
                1500,
                new HashSet<>(genres));

        HttpEntity<MovieRequest> entity = new HttpEntity<>(movieRequest, headers);

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
        MovieRequest movieRequest = new MovieRequest(
                "Inception",
                "A mind-bending heist movie set in somewhere",
                9.2,
                8.7,
                1500,
                new HashSet<>(genres));

        HttpEntity<MovieRequest> entity = new HttpEntity<>(movieRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies/1"),
                HttpMethod.PATCH, entity, String.class);

        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @Order(5)
    @DisplayName("Update Movie: Duplicate Title Conflict")
    public void testPatchMovieTakenTitle() {
        List<Long> genres = Arrays.asList(4L, 1L);
        MovieRequest movieRequest = new MovieRequest(
                "The Shawshank Redemption",
                "A mind-bending heist movie set in somewhere",
                9.2,
                8.7,
                1500,
                new HashSet<>(genres));

        HttpEntity<MovieRequest> entity = new HttpEntity<>(movieRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies/1"),
                HttpMethod.PATCH, entity, String.class);

        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    @Order(6)
    @DisplayName("Update Movie: Not Found")
    public void testPatchMovieNotFound() {
        List<Long> genres = Arrays.asList(4L, 1L);
        MovieRequest movieRequest = new MovieRequest(
                "The Skunk Redemption",
                "A mind-bending heist movie set in somewhere",
                9.2,
                8.7,
                1500,
                new HashSet<>(genres));

        HttpEntity<MovieRequest> entity = new HttpEntity<>(movieRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies/12"),
                HttpMethod.PATCH, entity, String.class);

        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @Order(7)
    @DisplayName("Get Movie: Successful")
    public void testGetMovieById() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies/1"),
                HttpMethod.GET, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @Order(8)
    @DisplayName("Get Movie: Not Found")
    public void testGetMovieByIdWrongId() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies/12"),
                HttpMethod.GET, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @Order(9)
    @DisplayName("Delete Movie: Successful")
    public void testDeleteMovie() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies/1"),
                HttpMethod.DELETE, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @Order(10)
    @DisplayName("Delete Movie: Not Found")
    public void testDeleteMovieWithById() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

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
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/movies"),
                HttpMethod.GET, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }
}
