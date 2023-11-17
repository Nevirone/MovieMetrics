package com.example.moviemetrics.api.service;

import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.model.Movie;
import com.example.moviemetrics.api.repository.IMovieRepository;
import com.example.moviemetrics.api.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ComponentScan(basePackages = "com.example.moviemetrics")
public class MovieServiceTest {

    @Autowired
    private MovieService movieService;
    @Autowired
    private IMovieRepository iMovieRepository;

    @BeforeEach
    void beforeEach() {
        iMovieRepository.deleteAll();
    }

    @Test
    @DisplayName("Create when should be created successfully")
    public void testCreateMovie() {
        // given
        Movie movie = new Movie(
                "New Title",
                "New Description",
                2.2, 2.2, 12
        );

        // when
        List<Movie> movies = movieService.getAllMovies();

        // then
        Movie result = assertDoesNotThrow(() -> movieService.createMovie(movie));
        List<Movie> moviesAfterCreate = movieService.getAllMovies();

        assertInstanceOf(Movie.class, result);
        assertEquals(movies.size() + 1, moviesAfterCreate.size());
    }

    @Test
    @DisplayName("Create when title is taken")
    public void testCreateMovieWhenTitleTaken() {
        // given
        Movie movie = new Movie(
                "New Title",
                "New Description",
                2.2, 2.2, 12
        );

        movieService.createMovie(movie);

        // when
        List<Movie> movies = movieService.getAllMovies();

        // then
        assertThrows(DataConflictException.class, () -> movieService.createMovie(movie));
        List<Movie> moviesAfterCreate = movieService.getAllMovies();

        assertEquals(movies.size(), moviesAfterCreate.size());
    }

    @Test
    @DisplayName("Get movie by title when movie exists")
    public void testGetMovieByTitleWhenMovieExists() {
        // given
        String title = "New Title";
        Movie movie = new Movie(
                title,
                "New Description",
                2.2, 2.2, 12
        );

        movieService.createMovie(movie);

        // when
        Movie m = movieService.getMovieByTitle(title);

        // then
        assertNotNull(m);
        assertInstanceOf(Movie.class, m);
    }

    @Test
    @DisplayName("Get movie by id when movie does not exist")
    public void testGetMovieByIdWhenMovieDoesNotExist() {
        // given
        Long id = 10L;

        // then
        assertThrows(NotFoundException.class, () -> movieService.getMovieById(id));
    }

    @Test
    @DisplayName("Get movie by title when movie does not exist")
    public void testGetMovieByTitleWhenMovieDoesNotExist() {
        // given
        String title = "New Title";

        // then
        assertThrows(NotFoundException.class, () -> movieService.getMovieByTitle(title));
    }

    @Test
    @DisplayName("Get all movies when movies exist")
    public void testGetAllMoviesWhenMoviesExist() {
        // given
        Movie movie = new Movie(
                "New Title",
                "New Description",
                2.2, 2.2, 12
        );

        movieService.createMovie(movie);

        // when
        List<Movie> movies = movieService.getAllMovies();

        // then
        assertInstanceOf(List.class, movies);
        assertFalse(movies.isEmpty());
    }

    @Test
    @DisplayName("Get all movies when no movies exist")
    public void testGetAllMoviesWhenNoMoviesExist() {
        // when
        List<Movie> movies = movieService.getAllMovies();

        // then
        assertInstanceOf(List.class, movies);
        assertTrue(movies.isEmpty());
    }

    @Test
    @DisplayName("Delete movie when movie exists")
    public void testDeleteMovieWhenExists() {
        // given
        Movie movie = new Movie(
                "New Title",
                "New Description",
                2.2, 2.2, 12
        );

        movieService.createMovie(movie);

        // when
        List<Movie> movies = movieService.getAllMovies();
        Movie target = movies.get(0);

        // then
        Movie result = assertDoesNotThrow(() -> movieService.deleteMovie(target.getId()));
        assertInstanceOf(Movie.class, result);
        assertEquals(target, result);
    }

    @Test
    @DisplayName("Delete movie when movie does not exist")
    public void testDeleteMovieWhenDoesNotExist() {
        // then
        assertThrows(NotFoundException.class, () -> movieService.deleteMovie(10L));
    }


    @Test
    @DisplayName("Patch movie when movie exists")
    public void testPatchMovieWhenExists() {
        // given
        String newTitle = "My changed title";
        Movie movie = new Movie(
                "New Title",
                "New Description",
                2.2, 2.2, 12
        );

        movieService.createMovie(movie);

        // when
        movie.setTitle(newTitle);

        // then
        Movie result = assertDoesNotThrow(() -> movieService.updateMovie(movie.getId(), movie));
        assertInstanceOf(Movie.class, result);
        assertEquals(newTitle, result.getTitle());
    }

    @Test
    @DisplayName("Patch movie when movie does not exist")
    public void testPatchMovieWhenDoesNotExist() {
        // then
        assertThrows(NotFoundException.class, () -> movieService.updateMovie(10L, null));
    }
}
