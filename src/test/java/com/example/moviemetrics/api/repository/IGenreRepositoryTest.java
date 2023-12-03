package com.example.moviemetrics.api.repository;

import com.example.moviemetrics.api.model.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class IGenreRepositoryTest {

    @Autowired
    private IGenreRepository genreRepository;
    @Test
    void itShouldCheckIfGenreExistsByName() {
        // given
        String name = "Action";
        Genre genre = Genre.builder().name(name).build();
        genreRepository.save(genre);

        // when
        Optional<Genre> found = genreRepository.findByName(name);

        // then
        assertThat(found.isPresent()).isTrue();
    }

    @Test
    void itShouldCheckIfGenreDoesNotExistByName() {
        // given
        String name = "Action";

        // when
        Optional<Genre> found = genreRepository.findByName(name);

        // then
        assertThat(found.isEmpty()).isTrue();
    }
}