package com.example.moviemetrics.api.service;

import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.repository.IGenreRepository;
import com.example.moviemetrics.api.request.GenreRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ComponentScan(basePackages = "com.example.moviemetrics")
public class GenreServiceTest {

    @Autowired
    private GenreService genreService;
    @Autowired
    private IGenreRepository iGenreRepository;

    @BeforeEach
    void beforeEach() {
        iGenreRepository.deleteAll();
    }

    @Test
    @DisplayName("Create genre should be created successfully")
    public void testCreateGenre() {
        // given
        GenreRequest genreRequest = GenreRequest
                .builder()
                .name("Action")
                .build();

        // when
        List<Genre> genres = genreService.getAllGenres();

        // then
        Genre result = assertDoesNotThrow(() -> genreService.createGenre(genreRequest));
        List<Genre> genresAfterCreate = genreService.getAllGenres();

        assertInstanceOf(Genre.class, result);
        assertEquals(genres.size() + 1, genresAfterCreate.size());
    }

    @Test
    @DisplayName("Create genre when name is taken")
    public void testCreateGenreWhenNameTaken() {
        // given
        GenreRequest genreRequest = GenreRequest
                .builder()
                .name("Action")
                .build();

        genreService.createGenre(genreRequest);

        // when
        List<Genre> genres = genreService.getAllGenres();

        // then
        assertThrows(DataConflictException.class, () -> genreService.createGenre(genreRequest));
        List<Genre> genresAfterCreate = genreService.getAllGenres();

        assertEquals(genres.size(), genresAfterCreate.size());
    }

    @Test
    @DisplayName("Get genre by name when genre exists")
    public void testGetGenreByNameWhenGenreExists() {
        // given
        String name = "Action";
        GenreRequest genreRequest = GenreRequest
                .builder()
                .name(name)
                .build();

        genreService.createGenre(genreRequest);

        // when
        Genre g = genreService.getGenreByName(name);

        // then
        assertNotNull(g);
        assertInstanceOf(Genre.class, g);
    }

    @Test
    @DisplayName("Get genre by id when genre does not exist")
    public void testGetGenreByIdWhenGenreDoesNotExist() {
        // given
        Long id = 10L;

        // then
        assertThrows(NotFoundException.class, () -> genreService.getGenreById(id));
    }

    @Test
    @DisplayName("Get genre by name when genre does not exist")
    public void testGetGenreByNameWhenGenreDoesNotExist() {
        // given
        String name = "Action";

        // then
        assertThrows(NotFoundException.class, () -> genreService.getGenreByName(name));
    }

    @Test
    @DisplayName("Get all genres when genres exist")
    public void testGetAllGenresWhenGenresExist() {
        // given
        GenreRequest genreRequest = GenreRequest
                .builder()
                .name("Action")
                .build();

        genreService.createGenre(genreRequest);

        // when
        List<Genre> genres = genreService.getAllGenres();

        // then
        assertInstanceOf(List.class, genres);
        assertFalse(genres.isEmpty());
    }

    @Test
    @DisplayName("Get all genres when no genres exist")
    public void testGetAllGenresWhenNoGenresExist() {
        // when
        List<Genre> genres = genreService.getAllGenres();

        // then
        assertInstanceOf(List.class, genres);
        assertTrue(genres.isEmpty());
    }

    @Test
    @DisplayName("Delete genre when genre exists")
    public void testDeleteGenreWhenExists() {
        // given
        GenreRequest genreRequest = GenreRequest
                .builder()
                .name("Action")
                .build();

        genreService.createGenre(genreRequest);

        // when
        List<Genre> genres = genreService.getAllGenres();
        Genre target = genres.get(0);

        // then
        Genre result = assertDoesNotThrow(() -> genreService.deleteGenre(target.getId()));
        assertInstanceOf(Genre.class, result);
        assertEquals(target, result);
    }

    @Test
    @DisplayName("Delete genre when genre does not exist")
    public void testDeleteGenreWhenDoesNotExist() {
        // then
        assertThrows(NotFoundException.class, () -> genreService.deleteGenre(10L));
    }

    @Test
    @DisplayName("Update genre when genre exists")
    public void testUpdateGenreWhenExists() {
        // given
        String newName = "Updated Action";
        GenreRequest genreRequest = GenreRequest
                .builder()
                .name("Action")
                .build();

        Genre genre = genreService.createGenre(genreRequest);

        // when
        genreRequest.setName(newName);

        // then
        Genre result = assertDoesNotThrow(() -> genreService.updateGenre(genre.getId(), genreRequest));
        assertInstanceOf(Genre.class, result);
        assertEquals(newName, result.getName());
    }

    @Test
    @DisplayName("Update genre when genre does not exist")
    public void testUpdateGenreWhenDoesNotExist() {
        // then
        assertThrows(NotFoundException.class, () -> genreService.updateGenre(10L, null));
    }
}
