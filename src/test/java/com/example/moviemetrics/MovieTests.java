package com.example.moviemetrics;

import com.example.moviemetrics.api.model.Movie;
import com.example.moviemetrics.api.repository.IMovieRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MovieTests {

    @Autowired
    private IMovieRepository movieRepository;

    @BeforeEach
    void beforEach() {

    }

    @AfterEach
    void afterEach() {
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

        //then
        assertThat(found).isNotEmpty();
    }

    @Test
    public void itShouldCheckMovieDoesNotExistByTitle() {
        // given
        String title = "My favourite movie";

        // when
        Optional<Movie> found = movieRepository.findByTitle(title);

        //then
        assertThat(found).isEmpty();
    }
}
