package com.example.moviemetrics.api.controller;

import com.example.moviemetrics.api.DTO.GenreDto;
import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
public class GenreController {
    final private GenreService genreService;

    @PostMapping
    public ResponseEntity<?> createGenre(@Valid @RequestBody GenreDto genreDto) {
        Genre createdGenre = genreService.createGenre(genreDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGenre);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGenreById(@PathVariable Long id) {
        Genre genre = genreService.getGenreById(id);
        return ResponseEntity.status(HttpStatus.OK).body(genre);
    }

    @GetMapping
    public ResponseEntity<?> getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();

        return ResponseEntity.status(HttpStatus.OK).body(genres);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateGenre(@PathVariable Long id, @Valid @RequestBody GenreDto genreDto) {
        Genre updatedGenre = genreService.updateGenre(id, genreDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedGenre);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

