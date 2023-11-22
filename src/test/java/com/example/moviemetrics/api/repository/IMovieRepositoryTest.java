package com.example.moviemetrics.api.repository;

import com.example.moviemetrics.api.model.Movie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
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
        Movie movie = Movie
                .builder()
                    .title(title)
                    .description("My description")
                    .popularity(2.2)
                    .voteAverage(2.2)
                    .voteCount(22)
                    .genres(new HashSet<>())
                    .build();

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
