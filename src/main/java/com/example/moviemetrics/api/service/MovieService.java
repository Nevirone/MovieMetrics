package com.example.moviemetrics.api.service;
import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public Movie createMovie(Movie newMovie) throws DataConflictException {
        if (movieRepository.findByTitle(newMovie.getTitle()).isPresent()) {
            throw new DataConflictException("Movie title taken");
        }

        return movieRepository.save(newMovie);
    }

    public Movie getMovieById(Long id) throws NotFoundException {
        return movieRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public Movie getMovieByTitle(String title) throws NotFoundException{
        return movieRepository.findByTitle(title).orElseThrow(NotFoundException::new);
    }
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie updateMovie(Long id, Movie movie) throws NotFoundException, DataConflictException{
        if(movieRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Movie not found");
        }

        Optional<Movie> titleExists = movieRepository.findByTitle(movie.getTitle());
        if (titleExists.isPresent() && !Objects.equals(titleExists.get().getId(), id)) {
            throw new DataConflictException("Movie title taken");
        }

        return movieRepository.save(movie);
    }

    public Movie deleteMovie(Long id) throws NotFoundException {
        Optional<Movie> movie = movieRepository.findById(id);
        if(movie.isEmpty()) {
            throw new NotFoundException("Movie not found");
        }

        movieRepository.deleteById(id);
        return movie.get();
    }
}
