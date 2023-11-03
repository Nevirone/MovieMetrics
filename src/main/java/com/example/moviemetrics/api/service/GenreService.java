package com.example.moviemetrics.api.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.example.moviemetrics.api.exception.GenreNameTakenException;
import com.example.moviemetrics.api.exception.GenreNotFoundException;

import com.example.moviemetrics.api.model.Genre;

import com.example.moviemetrics.api.repository.IGenreRepository;

@Service
public class GenreService {
    private final IGenreRepository genreRepository;

    @Autowired
    public GenreService(IGenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Genre createGenre(Genre newGenre) {
        if (genreRepository.findByName(newGenre.getName()).isPresent()) {
            throw new GenreNameTakenException();
        }

        return genreRepository.save(newGenre);
    }

    public Genre getGenreById(Long id) {
        return genreRepository.findById(id).orElseThrow(GenreNotFoundException::new);
    }

    public Genre getGenreByName(String name) {
        return genreRepository.findByName(name).orElseThrow(GenreNotFoundException::new);
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Genre updateGenre(Long id, Genre genre) {
        if(genreRepository.findById(id).isEmpty()) {
            throw new GenreNotFoundException();
        }

        Optional<Genre> nameExists = genreRepository.findByName(genre.getName());
        if (nameExists.isPresent() && !Objects.equals(nameExists.get().getId(), id)) {
            throw new GenreNameTakenException();
        }

        return genreRepository.save(genre);
    }

    public Genre deleteGenre(Long id) {
        Optional<Genre> genre = genreRepository.findById(id);
        if(genre.isEmpty()) {
            throw new GenreNotFoundException();
        }

        genreRepository.deleteById(id);
        return genre.get();
    }
}
