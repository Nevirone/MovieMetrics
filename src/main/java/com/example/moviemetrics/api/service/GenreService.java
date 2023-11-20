package com.example.moviemetrics.api.service;
import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
            throw new DataConflictException("Genre name taken");
        }

        return genreRepository.save(newGenre);
    }

    public Genre getGenreById(Long id) {
        return genreRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public Genre getGenreByName(String name) {
        return genreRepository.findByName(name).orElseThrow(NotFoundException::new);
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Genre updateGenre(Long id, Genre genre) {
        if(genreRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Genre not found");
        }

        Optional<Genre> nameExists = genreRepository.findByName(genre.getName());
        if (nameExists.isPresent() && !Objects.equals(nameExists.get().getId(), id)) {
            throw new DataConflictException("Genre name taken");
        }

        return genreRepository.save(genre);
    }

    @Transactional
    public Genre deleteGenre(Long id) {
        Optional<Genre> genre = genreRepository.findById(id);
        if(genre.isEmpty()) {
            throw new NotFoundException("Genre not found");
        }

        genreRepository.deleteById(id);
        return genre.get();
    }
}
