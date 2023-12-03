package com.example.moviemetrics.api.repository;

import com.example.moviemetrics.api.model.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class IMovieRepositoryTest {

    @Autowired
    private IMovieRepository movieRepository;
    @Test
    void itShouldCheckIfMovieExistsByTitle() {
        // given
        String title = "Saw";
        Movie movie = Movie.builder().title(title).description("Some desc")
                .popularity(9.2).voteAverage(8.5).voteCount(210).genres(new HashSet<>()).build();
        movieRepository.save(movie);

        // when
        Optional<Movie> found = movieRepository.findByTitle(title);

        // then
        assertThat(found.isPresent()).isTrue();
    }

    @Test
    void itShouldCheckIfMovieDoesNotExistByTitle() {
        // given
        String title = "Saw";

        // when
        Optional<Movie> found = movieRepository.findByTitle(title);

        // then
        assertThat(found.isEmpty()).isTrue();
    }
}