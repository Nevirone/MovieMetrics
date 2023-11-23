package com.example.moviemetrics.api.service;

import com.example.moviemetrics.api.DTO.DataStorageDto;
import com.example.moviemetrics.api.DTO.FilenameDto;
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

            userService.createUsers(dataStorageDto.getUserDtos());
            genreService.createGenres(dataStorageDto.getGenreDtos());
            movieService.createMovies(dataStorageDto.getMovieDtos());
    }
}
