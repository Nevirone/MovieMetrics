package com.example.moviemetrics.api.controller;
import com.example.moviemetrics.api.exception.*;
import com.example.moviemetrics.api.DTO.MovieDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.moviemetrics.api.model.Movie;

import com.example.moviemetrics.api.service.MovieService;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public ResponseEntity<?> createMovie(@Valid @RequestBody MovieDto movieDto) {
        try {
            Movie createdMovie = movieService.createMovie(movieDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
        } catch (DataConflictException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable Long id) {
        try {
            Movie movie = movieService.getMovieById(id);
            return ResponseEntity.status(HttpStatus.OK).body(movie);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();

        return ResponseEntity.status(HttpStatus.OK).body(movies);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateMovies(@PathVariable Long id, @Valid @RequestBody MovieDto movieDto) {
        try {
            Movie updatedMovie = movieService.updateMovie(id, movieDto);
            return ResponseEntity.status(HttpStatus.OK).body(updatedMovie);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (DataConflictException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        try {
            Movie deletedMovie = movieService.deleteMovie(id);
            return ResponseEntity.status(HttpStatus.OK).body(deletedMovie);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}
