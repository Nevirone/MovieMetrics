package com.example.moviemetrics.api.controllers;

import com.example.moviemetrics.MovieMetricsApplication;
import com.example.moviemetrics.api.model.User;
import com.example.moviemetrics.api.repository.IUserRepository;
import com.example.moviemetrics.api.request.GenreRequest;
import com.example.moviemetrics.api.request.LoginRequest;
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
    }

    @Test
    @Order(1)
    @DisplayName("Create Genre: Successful")
    public void testPostGenre() {
        GenreRequest genreRequest = new GenreRequest("Action");

        HttpEntity<GenreRequest> entity = new HttpEntity<>(genreRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres"),
                HttpMethod.POST, entity, String.class);

        genreRequest.setName("Drama");
        entity = new HttpEntity<>(genreRequest, headers);

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
        GenreRequest genreRequest = new GenreRequest("Action");

        HttpEntity<GenreRequest> entity = new HttpEntity<>(genreRequest, headers);

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
        GenreRequest genreRequest = new GenreRequest("xx");

        HttpEntity<GenreRequest> entity = new HttpEntity<>(genreRequest, headers);

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
        GenreRequest genreRequest = new GenreRequest("Comedy");

        HttpEntity<GenreRequest> entity = new HttpEntity<>(genreRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres/1"),
                HttpMethod.PATCH, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @Order(5)
    @DisplayName("Update Genre: Duplicate Name Conflict")
    public void testPatchGenreTakenName() {
        GenreRequest genreRequest = new GenreRequest("Comedy");

        HttpEntity<GenreRequest> entity = new HttpEntity<>(genreRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres/2"),
                HttpMethod.PATCH, entity, String.class);

        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    @Order(6)
    @DisplayName("Update Genre: Not Found")
    public void testPatchGenreWrongId() {
        GenreRequest genreRequest = new GenreRequest("Thriller");

        HttpEntity<GenreRequest> entity = new HttpEntity<>(genreRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres/12"),
                HttpMethod.PATCH, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @Order(7)
    @DisplayName("Get Genre: Successful")
    public void testGetGenreById() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres/1"),
                HttpMethod.GET, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @Order(8)
    @DisplayName("Get Genre: Not Found")
    public void testGetGenreByIdWrongId() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres/12"),
                HttpMethod.GET, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @Order(9)
    @DisplayName("Delete Genre: Successful")
    public void testDeleteGenres() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres/1"),
                HttpMethod.DELETE, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @Order(10)
    @DisplayName("Delete Genre: Not Found")
    public void testDeleteGenresWithById() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

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
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                getURL("/genres"),
                HttpMethod.GET, entity, String.class);


        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
        assertEquals(200, response.getStatusCode().value());
    }
}
