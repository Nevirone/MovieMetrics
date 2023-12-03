package com.example.moviemetrics.api.service;

import com.example.moviemetrics.api.DTO.GenreDto;
import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.exception.NotFoundException;
import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.repository.IGenreRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


class GenreServiceTest {
    private AutoCloseable autoCloseable;

    @Mock
    private IGenreRepository genreRepository;

    private GenreService genreService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        genreService = new GenreService(genreRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void canAddGenre() {
        // given
        GenreDto genreDto = GenreDto.builder().name("Action").build();

        // when
        genreService.createGenre(genreDto);

        // then
        ArgumentCaptor<Genre> genreArgumentCaptor = ArgumentCaptor.forClass(Genre.class);

        verify(genreRepository).save(genreArgumentCaptor.capture());

        Genre capturedGenre = genreArgumentCaptor.getValue();

        assertThat(capturedGenre.getName()).isEqualTo(genreDto.getName());
    }

    @Test
    void addingGenreWillThrowWhenNameIsTaken() {
        // given
        GenreDto genreDto = GenreDto.builder().name("Action").build();

        given(genreRepository.findByName(genreDto.getName())).willReturn(Optional.of(Genre.builder().build()));

        // when
        // then
        assertThatThrownBy(() -> genreService.createGenre(genreDto))
                .isInstanceOf(DataConflictException.class)
                .hasMessageContaining("Name")
                .hasMessageContaining(genreDto.getName())
                .hasMessageContaining("is taken");
    }

    @Test
    void canGetGenreById() {
        // given
        Long id = 2L;
        Optional<Genre> genreOptional = Optional.of(
                Genre.builder().name("Action").build()
        );

        given(genreRepository.findById(id)).willReturn(genreOptional);

        // when
        Genre found = genreService.getGenreById(id);

        // then
        assertThat(genreOptional.get().getName()).isEqualTo(found.getName());
    }

    @Test
    void gettingGenreByIdWillThrowWhenGenreIsNotFound() {
        // given
        Long id = 2L;

        given(genreRepository.findById(id)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> genreService.getGenreById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Genre with id")
                .hasMessageContaining(id.toString())
                .hasMessageContaining("not found");
    }

    @Test
    void canGetGenreByName() {
        // given
        Optional<Genre> genreOptional = Optional.of(
                Genre.builder().name("Action").build()
        );

        given(genreRepository.findByName(genreOptional.get().getName())).willReturn(genreOptional);

        // when
        Genre found = genreService.getGenreByName(genreOptional.get().getName());

        // then
        assertThat(genreOptional.get().getName()).isEqualTo(found.getName());
    }

    @Test
    void gettingGenreByNameWillThrowWhenUserIsNotFound() {
        // given
        String name = "Action";
        given(genreRepository.findByName(name)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> genreService.getGenreByName(name))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Genre with name")
                .hasMessageContaining(name)
                .hasMessageContaining("not found");
    }

    @Test
    void getAllGenres() {
        // when
        genreService.getAllGenres();

        // then
        verify(genreRepository).findAll();
    }

    @Test
    void canUpdateGenre() {
        // given
        Long id = 2L;
        GenreDto genreDto = GenreDto.builder().name("Action").build();

        given(genreRepository.findById(2L)).willReturn(Optional.of(Genre.builder().id(id).build()));
        given(genreRepository.findByName(genreDto.getName())).willReturn(Optional.empty());

        // when
        genreService.updateGenre(id, genreDto);

        // then
        ArgumentCaptor<Genre> genreArgumentCaptor = ArgumentCaptor.forClass(Genre.class);

        verify(genreRepository).findById(id);
        verify(genreRepository).findByName(genreDto.getName());

        verify(genreRepository).save(genreArgumentCaptor.capture());

        Genre capturedGenre = genreArgumentCaptor.getValue();

        assertThat(capturedGenre.getId()).isEqualTo(id);
        assertThat(capturedGenre.getName()).isEqualTo(genreDto.getName());
    }

    @Test
    void updatingGenreWillThrowWhenGenreNotFound() {
        // given
        Long id = 2L;
        GenreDto genreDto = GenreDto.builder().name("Action").build();

        given(genreRepository.findById(2L)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> genreService.updateGenre(id, genreDto))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Genre with id")
                .hasMessageContaining(id.toString())
                .hasMessageContaining("not found");
    }

    @Test
    void updatingGenreWillThrowWhenNameIsTaken() {
        // given
        Long id = 2L;
        GenreDto genreDto = GenreDto.builder().name("Action").build();

        given(genreRepository.findById(2L)).willReturn(Optional.of(Genre.builder().id(id).build()));
        given(genreRepository.findByName(genreDto.getName())).willReturn(Optional.of(Genre.builder().build()));

        // when
        // then
        assertThatThrownBy(() -> genreService.updateGenre(id, genreDto))
                .isInstanceOf(DataConflictException.class)
                .hasMessageContaining("Name")
                .hasMessageContaining(genreDto.getName())
                .hasMessageContaining("is taken");
    }

    @Test
    void canDeleteGenre() {
        // given
        Long id = 2L;

        given(genreRepository.findById(id)).willReturn(Optional.of(Genre.builder().build()));

        // when
        genreService.deleteGenre(id);

        // then
        verify(genreRepository).deleteById(id);
    }

    @Test
    void deletingGenreWillThrowWhenGenreNotFound() {
        // given
        Long id = 2L;

        given(genreRepository.findById(id)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> genreService.deleteGenre(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Genre with id")
                .hasMessageContaining(id.toString())
                .hasMessageContaining("not found");
    }
}