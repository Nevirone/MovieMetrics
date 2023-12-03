package com.example.moviemetrics.api.service;

import com.example.moviemetrics.api.DTO.MovieDto;
import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.model.Movie;
import com.example.moviemetrics.api.repository.IGenreRepository;
import com.example.moviemetrics.api.repository.IMovieRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


class MovieServiceTest {
    private AutoCloseable autoCloseable;

    @Mock
    private IMovieRepository movieRepository;
    @Mock
    private IGenreRepository genreRepository;

    private MovieService movieService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        movieService = new MovieService(movieRepository, genreRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void canAddMovie() {
        // give
        List<String> genreNames = Arrays.asList("Action", "Horror");
        MovieDto movieDto = MovieDto.builder()
                .title("Saw")
                .description("Cool movie")
                .popularity(9.2)
                .voteAverage(8.5)
                .voteCount(120)
                .genres(new HashSet<>(genreNames))
                .build();

        given(genreRepository.findByName(genreNames.get(0))).willReturn(
                Optional.of(
                        Genre.builder()
                                .id(1L)
                                .name(genreNames.get(0))
                                .build()
                )
        );

        given(genreRepository.findByName(genreNames.get(1))).willReturn(
                Optional.of(
                        Genre.builder()
                                .id(2L)
                                .name(genreNames.get(1))
                                .build()
                )
        );

        given(movieRepository.findByTitle(movieDto.getTitle())).willReturn(Optional.empty());

        // when
        movieService.createMovie(movieDto);

        // then
        ArgumentCaptor<Movie> movieArgumentCaptor = ArgumentCaptor.forClass(Movie.class);

        verify(movieRepository).save(movieArgumentCaptor.capture());

        Movie capturedMovie = movieArgumentCaptor.getValue();

        assertThat(capturedMovie.getTitle()).isEqualTo(movieDto.getTitle());
        assertThat(capturedMovie.getDescription()).isEqualTo(movieDto.getDescription());
        assertThat(capturedMovie.getVoteCount()).isEqualTo(movieDto.getVoteCount());
        assertThat(capturedMovie.getVoteAverage()).isEqualTo(movieDto.getVoteAverage());
        assertThat(capturedMovie.getPopularity()).isEqualTo(movieDto.getPopularity());
    }

    @Test
    void canAddMovieWhenGenreNotFound() {
        // give
        List<String> genreNames = List.of("Action");
        MovieDto movieDto = MovieDto.builder()
                .title("Saw")
                .description("Cool movie")
                .popularity(9.2)
                .voteAverage(8.5)
                .voteCount(120)
                .genres(new HashSet<>(genreNames))
                .build();

        given(genreRepository.findByName(genreNames.get(0))).willReturn(
                Optional.empty()
        );

        given(movieRepository.findByTitle(movieDto.getTitle())).willReturn(Optional.empty());

        given(genreRepository.save(Genre.builder().name(genreNames.get(0)).build()))
                .willReturn(Genre.builder().id(1L).name(genreNames.get(0)).build());
        // when
        movieService.createMovie(movieDto);

        // then
        ArgumentCaptor<Movie> movieArgumentCaptor = ArgumentCaptor.forClass(Movie.class);

        verify(movieRepository).save(movieArgumentCaptor.capture());

        Movie capturedMovie = movieArgumentCaptor.getValue();

        assertThat(capturedMovie.getTitle()).isEqualTo(movieDto.getTitle());
        assertThat(capturedMovie.getDescription()).isEqualTo(movieDto.getDescription());
        assertThat(capturedMovie.getVoteCount()).isEqualTo(movieDto.getVoteCount());
        assertThat(capturedMovie.getVoteAverage()).isEqualTo(movieDto.getVoteAverage());
        assertThat(capturedMovie.getPopularity()).isEqualTo(movieDto.getPopularity());
    }

    @Test
    void addingMovieWillThrowWhenTitleIsTaken() {
        // give
        List<String> genreNames = Arrays.asList("Action", "Horror");
        MovieDto movieDto = MovieDto.builder()
                .title("Saw")
                .description("Cool movie")
                .popularity(9.2)
                .voteAverage(8.5)
                .voteCount(120)
                .genres(new HashSet<>(genreNames))
                .build();


        given(movieRepository.findByTitle(movieDto.getTitle())).willReturn(Optional.of(Movie.builder().build()));

        // when
        // then
        assertThatThrownBy(() -> movieService.createMovie(movieDto))
                .isInstanceOf(DataConflictException.class)
                .hasMessageContaining("Title")
                .hasMessageContaining(movieDto.getTitle())
                .hasMessageContaining("is taken");
    }

    @Test
    void canGetMovieById() {
        // given
        Long id = 2L;
        Optional<Movie> movieOptional = Optional.of(
                Movie.builder()
                        .title("Saw")
                        .description("A cool movie")
                        .voteCount(120)
                        .voteAverage(9.2)
                        .popularity(8.5)
                        .genres(new HashSet<>())
                        .build()
        );

        given(movieRepository.findById(id)).willReturn(movieOptional);

        // when
        Movie found = movieService.getMovieById(id);

        // then
        assertThat(movieOptional.get().getTitle()).isEqualTo(found.getTitle());
        assertThat(movieOptional.get().getDescription()).isEqualTo(found.getDescription());
        assertThat(movieOptional.get().getVoteCount()).isEqualTo(found.getVoteCount());
        assertThat(movieOptional.get().getVoteAverage()).isEqualTo(found.getVoteAverage());
        assertThat(movieOptional.get().getPopularity()).isEqualTo(found.getPopularity());
    }

    @Test
    void gettingMovieByIdWillThrowWhenMovieIsNotFound() {
        // given
        Long id = 2L;

        given(movieRepository.findById(id)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> movieService.getMovieById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Movie with id")
                .hasMessageContaining(id.toString())
                .hasMessageContaining("not found");
    }

    @Test
    void canGetMovieByName() {
        // given
        Optional<Movie> movieOptional = Optional.of(
                Movie.builder()
                        .title("Saw")
                        .description("A cool movie")
                        .voteCount(120)
                        .voteAverage(9.2)
                        .popularity(8.5)
                        .genres(new HashSet<>())
                        .build()
        );

        given(movieRepository.findByTitle(movieOptional.get().getTitle())).willReturn(movieOptional);

        // when
        Movie found = movieService.getMovieByTitle(movieOptional.get().getTitle());

        // then
        assertThat(movieOptional.get().getTitle()).isEqualTo(found.getTitle());
        assertThat(movieOptional.get().getDescription()).isEqualTo(found.getDescription());
        assertThat(movieOptional.get().getVoteCount()).isEqualTo(found.getVoteCount());
        assertThat(movieOptional.get().getVoteAverage()).isEqualTo(found.getVoteAverage());
        assertThat(movieOptional.get().getPopularity()).isEqualTo(found.getPopularity());
    }

    @Test
    void gettingMovieByTitleWillThrowWhenMovieIsNotFound() {
        // given
        String title = "Saw";
        given(movieRepository.findByTitle(title)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> movieService.getMovieByTitle(title))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Movie with title")
                .hasMessageContaining(title)
                .hasMessageContaining("not found");
    }

    @Test
    void getAllMovies() {
        // when
        movieService.getAllMovies();

        // then
        verify(movieRepository).findAll();
    }

    @Test
    void canUpdateMovie() {
        // given
        Long id = 2L;
        List<String> genreNames = Arrays.asList("Action", "Horror");
        MovieDto movieDto = MovieDto.builder()
                .title("Saw")
                .description("Cool movie")
                .popularity(9.2)
                .voteAverage(8.5)
                .voteCount(120)
                .genres(new HashSet<>(genreNames))
                .build();


        given(genreRepository.findByName(genreNames.get(0))).willReturn(
                Optional.of(
                        Genre.builder()
                                .id(1L)
                                .name(genreNames.get(0))
                                .build()
                )
        );

        given(genreRepository.findByName(genreNames.get(1))).willReturn(
                Optional.of(
                        Genre.builder()
                                .id(2L)
                                .name(genreNames.get(1))
                                .build()
                )
        );

        given(movieRepository.findById(id)).willReturn(Optional.of(Movie.builder().id(id).build()));
        given(movieRepository.findByTitle(movieDto.getTitle())).willReturn(Optional.empty());

        // when
        movieService.updateMovie(id, movieDto);

        // then
        ArgumentCaptor<Movie> movieArgumentCaptor = ArgumentCaptor.forClass(Movie.class);

        verify(movieRepository).findById(id);
        verify(movieRepository).findByTitle(movieDto.getTitle());

        verify(movieRepository).save(movieArgumentCaptor.capture());

        Movie capturedMovie = movieArgumentCaptor.getValue();

        assertThat(capturedMovie.getId()).isEqualTo(id);
        assertThat(capturedMovie.getTitle()).isEqualTo(movieDto.getTitle());
        assertThat(capturedMovie.getDescription()).isEqualTo(movieDto.getDescription());
        assertThat(capturedMovie.getVoteAverage()).isEqualTo(movieDto.getVoteAverage());
        assertThat(capturedMovie.getVoteCount()).isEqualTo(movieDto.getVoteCount());
        assertThat(capturedMovie.getPopularity()).isEqualTo(movieDto.getPopularity());
    }

    @Test
    void updatingMovieWillThrowWhenMovieNotFound() {
        // given
        Long id = 2L;
        List<String> genreNames = Arrays.asList("Action", "Horror");
        MovieDto movieDto = MovieDto.builder()
                .title("Saw")
                .description("Cool movie")
                .popularity(9.2)
                .voteAverage(8.5)
                .voteCount(120)
                .genres(new HashSet<>(genreNames))
                .build();


        given(movieRepository.findById(2L)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> movieService.updateMovie(id, movieDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Movie with id")
                .hasMessageContaining(id.toString())
                .hasMessageContaining("not found");
    }

    @Test
    void updatingGenreWillThrowWhenNameIsTaken() {
        // given
        Long id = 2L;
        List<String> genreNames = Arrays.asList("Action", "Horror");
        MovieDto movieDto = MovieDto.builder()
                .title("Saw")
                .description("Cool movie")
                .popularity(9.2)
                .voteAverage(8.5)
                .voteCount(120)
                .genres(new HashSet<>(genreNames))
                .build();

        given(movieRepository.findById(2L)).willReturn(Optional.of(Movie.builder().id(id).build()));
        given(movieRepository.findByTitle(movieDto.getTitle())).willReturn(Optional.of(Movie.builder().build()));

        // when
        // then
        assertThatThrownBy(() -> movieService.updateMovie(id, movieDto))
                .isInstanceOf(DataConflictException.class)
                .hasMessageContaining("Title")
                .hasMessageContaining(movieDto.getTitle())
                .hasMessageContaining("is taken");
    }

    @Test
    void canDeleteMovie() {
        // given
        Long id = 2L;

        given(movieRepository.findById(id)).willReturn(Optional.of(Movie.builder().build()));

        // when
        movieService.deleteMovie(id);

        // then
        verify(movieRepository).deleteById(id);
    }

    @Test
    void deletingMovieWillThrowWhenMovieNotFound() {
        // given
        Long id = 2L;

        given(movieRepository.findById(id)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> movieService.deleteMovie(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Movie with id")
                .hasMessageContaining(id.toString())
                .hasMessageContaining("not found");
    }
}