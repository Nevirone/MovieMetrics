package com.example.moviemetrics.api.repository;

import com.example.moviemetrics.MovieMetricsApplication;
import com.example.moviemetrics.api.model.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IGenreRepositoryTest {

    @Autowired
    private IGenreRepository genreRepository;

    @BeforeEach
    void beforeEach() {
        genreRepository.deleteAll();
    }

    @Test
    public void itShouldCheckGenreExistsByName() {
        // given
        String name = "Action";
        Genre genre = new Genre(name);

        genreRepository.save(genre);

        // when
        Optional<Genre> found = genreRepository.findByName(name);

        // then
        assertTrue(found.isPresent());
        assertInstanceOf(Genre.class, found.get());
    }

    @Test
    public void itShouldCheckMovieDoesNotExistByTitle() {
        // given
        String name = "Action";

        // when
        Optional<Genre> found = genreRepository.findByName(name);

        // then
        assertTrue(found.isEmpty());
    }
}
