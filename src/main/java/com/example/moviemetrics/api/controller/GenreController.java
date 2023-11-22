package com.example.moviemetrics.api.controller;
import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import com.example.moviemetrics.api.request.GenreRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.moviemetrics.api.model.Genre;

import com.example.moviemetrics.api.service.GenreService;

@RestController
@RequestMapping("/genres")
public class GenreController {
    final private GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    public ResponseEntity<?> createGenre(@Valid @RequestBody GenreRequest genreRequest) {
        try {
            Genre createdGenre = genreService.createGenre(genreRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdGenre);
        } catch (DataConflictException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGenreById(@PathVariable Long id) {
        try {
            Genre genre = genreService.getGenreById(id);
            return ResponseEntity.status(HttpStatus.OK).body(genre);
        } catch(NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();

        return ResponseEntity.status(HttpStatus.OK).body(genres);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateGenre(@PathVariable Long id, @Valid @RequestBody GenreRequest genreRequest) {
        try {
            Genre updatedGenre = genreService.updateGenre(id, genreRequest);
            return ResponseEntity.status(HttpStatus.OK).body(updatedGenre);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (DataConflictException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteGenre(@PathVariable Long id) {
        try {
            Genre deletedGenre = genreService.deleteGenre(id);
            return ResponseEntity.status(HttpStatus.OK).body(deletedGenre);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}

