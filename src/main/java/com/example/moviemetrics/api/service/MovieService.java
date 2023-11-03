package com.example.moviemetrics.api.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.example.moviemetrics.api.exception.MovieNotFoundException;
import com.example.moviemetrics.api.exception.MovieTitleTakenException;

import com.example.moviemetrics.api.model.Movie;

import com.example.moviemetrics.api.repository.IGenreRepository;
import com.example.moviemetrics.api.repository.IMovieRepository;

@Service
public class MovieService {
    private final IMovieRepository movieRepository;

    @Autowired
    public MovieService(IMovieRepository movieRepository, IGenreRepository genreRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie createMovie(Movie newMovie) {
        if (movieRepository.findByTitle(newMovie.getTitle()).isPresent()) {
            throw new MovieTitleTakenException();
        }

        return movieRepository.save(newMovie);
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElseThrow(MovieNotFoundException::new);
    }

    public Movie getMovieByTitle(String title) {
        return movieRepository.findByTitle(title).orElseThrow(MovieNotFoundException::new);
    }
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie updateMovie(Long id, Movie movie) {
        if(movieRepository.findById(id).isEmpty()) {
            throw new MovieNotFoundException();
        }

        Optional<Movie> titleExists = movieRepository.findByTitle(movie.getTitle());
        if (titleExists.isPresent() && !Objects.equals(titleExists.get().getId(), id)) {
            throw new MovieTitleTakenException();
        }

        return movieRepository.save(movie);
    }

    public Movie deleteMovie(Long id) {
        Optional<Movie> movie = movieRepository.findById(id);
        if(movie.isEmpty()) {
            throw new MovieNotFoundException();
        }

        movieRepository.deleteById(id);
        return movie.get();
    }
}
