package com.example.moviemetrics.api.repository;

import com.example.moviemetrics.api.model.Movie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IMovieRepositoryTest {

    @Autowired
    private IMovieRepository movieRepository;

    @BeforeEach
    void beforeEach() {
        movieRepository.deleteAll();
    }

    @Test
    public void itShouldCheckMovieExistsByTitle() {
        // given
        String title = "My favourite movie";
        Movie movie = new Movie(
                title,
                "Some description",
                2.1, 2.1, 20);

        movieRepository.save(movie);

        // when
        Optional<Movie> found = movieRepository.findByTitle(title);

        // then
        assertTrue(found.isPresent());
        assertInstanceOf(Movie.class, found.get());
    }

    @Test
    public void itShouldCheckMovieDoesNotExistByTitle() {
        // given
        String title = "My favourite movie";

        // when
        Optional<Movie> found = movieRepository.findByTitle(title);

        // then
        assertTrue(found.isEmpty());
    }
}
