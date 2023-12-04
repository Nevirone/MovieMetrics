package com.example.moviemetrics.api.controller;

import com.example.moviemetrics.api.DTO.MovieDto;
import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.model.Movie;
import com.example.moviemetrics.api.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<?> createMovie(@Valid @RequestBody MovieDto movieDto) {
        Movie createdMovie = movieService.createMovie(movieDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.status(HttpStatus.OK).body(movie);
    }

    @GetMapping
    public ResponseEntity<?> getAllMovies(
            @RequestParam Optional<Integer> voteCountFrom,
            @RequestParam Optional<Integer> voteCountTo,
            @RequestParam Optional<Double> voteAverageFrom,
            @RequestParam Optional<Double> voteAverageTo,
            @RequestParam Optional<Double> popularityFrom,
            @RequestParam Optional<Double> popularityTo,
            @RequestParam Optional<List<String>> genres
    ) {
        List<Movie> movies = movieService.getAllMovies();

        movies = movies.stream().filter(movie -> {
            if (voteCountFrom.isPresent() && movie.getVoteCount() < voteCountFrom.get()) return false;
            if (voteCountTo.isPresent() && movie.getVoteCount() > voteCountTo.get()) return false;

            if (voteAverageFrom.isPresent() && movie.getVoteAverage() < voteAverageFrom.get()) return false;
            if (voteAverageTo.isPresent() && movie.getVoteAverage() > voteAverageTo.get()) return false;

            if (popularityFrom.isPresent() && movie.getPopularity() < popularityFrom.get()) return false;
            if (popularityTo.isPresent() && movie.getPopularity() > popularityTo.get()) return false;

            if (genres.isPresent()) {
                for (Genre genre : movie.getGenres())
                    if (genres.get().contains(genre.getName())) return true;
                return false;
            }

            return true;
        }).toList();
        return ResponseEntity.status(HttpStatus.OK).body(movies);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateMovies(@PathVariable Long id, @Valid @RequestBody MovieDto movieDto) {
        Movie updatedMovie = movieService.updateMovie(id, movieDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedMovie);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok().build();
    }
}
