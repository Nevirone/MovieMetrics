package com.example.moviemetrics.api.service;
import com.example.moviemetrics.api.DTO.MovieDto;
import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import com.example.moviemetrics.api.DTO.GenreDto;
import com.example.moviemetrics.api.model.Movie;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public Genre createGenre(GenreDto genreDto) throws DataConflictException {
        if (genreRepository.findByName(genreDto.getName()).isPresent()) {
            throw new DataConflictException("Genre name taken");
        }

        Genre genre = Genre
                .builder()
                .name(genreDto.getName())
                .build();

        return genreRepository.save(genre);
    }


    public List<Genre> createGenres(List<GenreDto> genreDtoList) throws DataConflictException {
        List<Genre> genres = new ArrayList<>();

        System.out.println("Loading genres:");
        for(GenreDto genreDto : genreDtoList)
            try {
                genres.add(createGenre(genreDto));
            } catch (DataConflictException ex) {
                System.out.println("Name exists: " + genreDto.getName());
            }

        return genres;
    }

    public Genre getGenreById(Long id) throws NotFoundException {
        return genreRepository.findById(id).orElseThrow(() -> new NotFoundException("Genre not found"));
    }

    public Genre getGenreByName(String name) throws NotFoundException {
        return genreRepository.findByName(name).orElseThrow(() -> new NotFoundException("Genre not found"));
    }

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Genre updateGenre(Long id, GenreDto genreDto) throws NotFoundException, DataConflictException {
        if(genreRepository.findById(id).isEmpty())
            throw new NotFoundException("Genre not found");

        Optional<Genre> nameExists = genreRepository.findByName(genreDto.getName());
        if (nameExists.isPresent() && !Objects.equals(nameExists.get().getId(), id))
            throw new DataConflictException("Genre name taken");

        Genre genre = Genre
                .builder()
                .id(id)
                .name(genreDto.getName())
                .build();

        return genreRepository.save(genre);
    }

    @Transactional
    public Genre deleteGenre(Long id) throws NotFoundException {
        Optional<Genre> genre = genreRepository.findById(id);
        if(genre.isEmpty())
            throw new NotFoundException("Genre not found");


        genreRepository.deleteById(id);
        return genre.get();
    }
}
