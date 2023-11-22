package com.example.moviemetrics.api.service;
import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import com.example.moviemetrics.api.request.MovieRequest;
import jakarta.transaction.Transactional;
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
    private final GenreService genreService;

    @Autowired
    public MovieService(IMovieRepository movieRepository, GenreService genreService) {
        this.movieRepository = movieRepository;
        this.genreService = genreService;
    }

    public Movie createMovie(MovieRequest movieRequest) throws DataConflictException {
        if (movieRepository.findByTitle(movieRequest.getTitle()).isPresent())
            throw new DataConflictException("Movie title taken");

        Movie movie = Movie
                .builder()
                .title(movieRequest.getTitle())
                .description(movieRequest.getDescription())
                .popularity(movieRequest.getPopularity())
                .voteAverage(movieRequest.getVoteAverage())
                .voteCount(movieRequest.getVoteCount())
                .build();

        movie.setGenresByIds(movieRequest.getGenreIds(), genreService);

        return movieRepository.save(movie);
    }

    public Movie getMovieById(Long id) throws NotFoundException {
        return movieRepository.findById(id).orElseThrow(() -> new NotFoundException(("Movie not found")));
    }

    public Movie getMovieByTitle(String title) throws NotFoundException{
        return movieRepository.findByTitle(title).orElseThrow(() -> new NotFoundException(("Movie not found")));
    }
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie updateMovie(Long id, MovieRequest movieRequest) throws NotFoundException, DataConflictException{
        if(movieRepository.findById(id).isEmpty())
            throw new NotFoundException("Movie not found");


        Optional<Movie> titleExists = movieRepository.findByTitle(movieRequest.getTitle());
        if (titleExists.isPresent() && !Objects.equals(titleExists.get().getId(), id))
            throw new DataConflictException("Movie title taken");

        Movie movie = Movie
                .builder()
                .id(id)
                .title(movieRequest.getTitle())
                .description(movieRequest.getDescription())
                .popularity(movieRequest.getPopularity())
                .voteAverage(movieRequest.getVoteAverage())
                .voteCount(movieRequest.getVoteCount())
                .build();

        movie.setGenresByIds(movieRequest.getGenreIds(), genreService);

        return movieRepository.save(movie);
    }

    @Transactional
    public Movie deleteMovie(Long id) throws NotFoundException {
        Optional<Movie> movie = movieRepository.findById(id);
        if(movie.isEmpty())
            throw new NotFoundException("Movie not found");


        movieRepository.deleteById(id);
        return movie.get();
    }
}
