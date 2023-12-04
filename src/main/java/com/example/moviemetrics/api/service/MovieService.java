package com.example.moviemetrics.api.service;

import com.example.moviemetrics.api.DTO.MovieDto;
import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.model.Movie;
import com.example.moviemetrics.api.repository.IGenreRepository;
import com.example.moviemetrics.api.repository.IMovieRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final IMovieRepository movieRepository;
    private final IGenreRepository genreRepository;

    private Movie getMovieFromMovieDto(MovieDto movieDto) {
        Set<Genre> genres = new HashSet<>();

        for(String genreName : movieDto.getGenres()) {
            Optional<Genre> genre = genreRepository.findByName(genreName);

            if (genre.isEmpty()) {
                Genre created = genreRepository.save(Genre.builder().name(genreName).build());
                genres.add(created);
            } else {
                genres.add(genre.get());
            }
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
            throw new DataConflictException("Title " + movieDto.getTitle() + " is taken");

        return movieRepository.save(getMovieFromMovieDto(movieDto));
    }

    public Movie getMovieById(Long id) throws NotFoundException {
        return movieRepository.findById(id).orElseThrow(() -> new NotFoundException(("Movie with id " + id + " not found")));
    }

    public Movie getMovieByTitle(String title) throws NotFoundException{
        return movieRepository.findByTitle(title).orElseThrow(() -> new NotFoundException(("Movie with title " + title + " not found")));
    }
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie updateMovie(Long id, MovieDto movieDto) throws NotFoundException, DataConflictException{
        if(movieRepository.findById(id).isEmpty())
            throw new NotFoundException("Movie with id " + id + " not found");

        Optional<Movie> titleExists = movieRepository.findByTitle(movieDto.getTitle());
        if (titleExists.isPresent() && !Objects.equals(titleExists.get().getId(), id))
            throw new DataConflictException("Title " + movieDto.getTitle() + " is taken");

        Movie movie = getMovieFromMovieDto(movieDto);
        movie.setId(id);

        return movieRepository.save(movie);
    }

    @Transactional
    public void deleteMovie(Long id) throws NotFoundException {
        Optional<Movie> movie = movieRepository.findById(id);
        if(movie.isEmpty())
            throw new NotFoundException("Movie with id " + id + " not found");

        movieRepository.deleteById(id);
    }
}
