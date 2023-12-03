package com.example.moviemetrics.api.service;
import com.example.moviemetrics.api.DTO.GenreDto;
import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import com.example.moviemetrics.api.DTO.MovieDto;
import com.example.moviemetrics.api.model.Genre;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import com.example.moviemetrics.api.model.Movie;

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

    private Movie getMovieFromMovieDto(MovieDto movieDto) {
        Set<Genre> genres = new HashSet<>();

        for(String genreName : movieDto.getGenres()) {
            Genre genre;
            try {
                genre = genreService.getGenreByName(genreName);
            } catch (NotFoundException ex) {
                GenreDto dto = GenreDto.builder().name(genreName).build();
                genre = genreService.createGenre(dto);
            }
            genres.add(genre);
        }

        return Movie
                .builder()
                .title(movieDto.getTitle())
                .description(movieDto.getDescription())
                .genres(genres)
                .popularity(movieDto.getPopularity())
                .voteAverage(movieDto.getVoteAverage())
                .voteCount(movieDto.getVoteCount())
                .build();
    }
    public Movie createMovie(MovieDto movieDto) throws DataConflictException {
        if (movieRepository.findByTitle(movieDto.getTitle()).isPresent())
            throw new DataConflictException("Movie title taken");

        return movieRepository.save(getMovieFromMovieDto(movieDto));
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

    public Movie updateMovie(Long id, MovieDto movieDto) throws NotFoundException, DataConflictException{
        if(movieRepository.findById(id).isEmpty())
            throw new NotFoundException("Movie not found");

        Optional<Movie> titleExists = movieRepository.findByTitle(movieDto.getTitle());
        if (titleExists.isPresent() && !Objects.equals(titleExists.get().getId(), id))
            throw new DataConflictException("Movie title taken");

        Movie movie = getMovieFromMovieDto(movieDto);
        movie.setId(id);

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
