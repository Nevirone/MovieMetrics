package com.example.moviemetrics.api.controller;
import com.example.moviemetrics.api.request.GenreRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.example.moviemetrics.api.exception.GenreNameTakenException;
import com.example.moviemetrics.api.exception.GenreNotFoundException;

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
    public ResponseEntity<?> createGenre(@RequestBody Genre newGenre) {
        try {
            Genre createdGenre = genreService.createGenre(newGenre);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdGenre);
        } catch (GenreNameTakenException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGenreById(@PathVariable Long id) {
        try {
            Genre genre = genreService.getGenreById(id);
            return ResponseEntity.status(HttpStatus.OK).body(genre);
        } catch(GenreNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();

        return ResponseEntity.status(HttpStatus.OK).body(genres);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGenre(@PathVariable Long id, @RequestBody GenreRequest genreRequest) {
        Genre newGenre = genreRequest.getGenre();
        newGenre.setId(id);

        try {
            Genre updatedGenre = genreService.updateGenre(id, genreRequest.getGenre());
            return ResponseEntity.status(HttpStatus.OK).body(updatedGenre);
        } catch (GenreNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (GenreNameTakenException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGenre(@PathVariable Long id) {
        try {
            Genre deletedGenre = genreService.deleteGenre(id);
            return ResponseEntity.status(HttpStatus.OK).body(deletedGenre);
        } catch (GenreNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
}

