package com.example.moviemetrics.api.service;

import com.example.moviemetrics.api.DTO.*;
import com.example.moviemetrics.api.exception.DataConflictException;
import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.model.Movie;
import com.example.moviemetrics.api.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataService {
    private final UserService userService;
    private final GenreService genreService;
    private final MovieService movieService;

    private final String fileDirectory = "data_dumps";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");

    public byte[] getDatabaseData(String filename) throws IOException {
        List<User> users = userService.getAllUsers();
        List<Genre> genres = genreService.getAllGenres();
        List<Movie> movies = movieService.getAllMovies();

        DataStorageDto dataStorageDto = new DataStorageDto(genres, movies, users);

        Path jsonFilePath = Path.of(fileDirectory, filename);

        String jsonValue = dataStorageDto.toJsonString();
        byte[] jsonData = jsonValue.getBytes();

        Files.write(jsonFilePath, jsonData, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return jsonData;
    }

    public void loadDatabaseData(String filename) throws IOException {
        Path jsonFilePath = Path.of(fileDirectory, filename);
        String jsonValue = new String(Files.readAllBytes(jsonFilePath));

        DataStorageDto dataStorageDto = DataStorageDto.readFromJsonString(jsonValue);

        System.out.println("Loading users...");
        for(UserDto userDto : dataStorageDto.getUserDtos()){
            try {
                userService.createUser(userDto);
            } catch (DataConflictException ex) {
                System.out.println(ex.getMessage());
            }
        }
        System.out.println("Users loaded");

        System.out.println("Loading genres...");
        for(GenreDto genreDto : dataStorageDto.getGenreDtos()){
            try {
                genreService.createGenre(genreDto);
            } catch (DataConflictException ex) {
                System.out.println(ex.getMessage());
            }
        }
        System.out.println("Genres loaded");

        System.out.println("Loading movies...");
        for(MovieDto movieDto : dataStorageDto.getMovieDtos()){
            try {
                movieService.createMovie(movieDto);
            } catch (DataConflictException ex) {
                System.out.println(ex.getMessage());
            }
        }
        System.out.println("Movies loaded");
    }
}
