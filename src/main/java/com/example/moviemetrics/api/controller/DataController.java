package com.example.moviemetrics.api.controller;

import com.example.moviemetrics.api.DTO.FilenameDto;
import com.example.moviemetrics.api.service.DataService;
import com.example.moviemetrics.api.service.GenreService;
import com.example.moviemetrics.api.service.MovieService;
import com.example.moviemetrics.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/data")
@PreAuthorize("hasAuthority('ADMIN')")
public class DataController {
    private final UserService userService;
    private final GenreService genreService;
    private final MovieService movieService;
    private final DataService dataService;


    private final String fileDirectory = "data_dumps";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
    @GetMapping("/save")
    public ResponseEntity<?> getDatabaseData() {
        try {
            String filename = "data_dump_" + dateFormat.format(new Date()) + ".json";
            byte[] jsonData = dataService.getDatabaseData(filename);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDispositionFormData("attachment", filename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonData);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/load")
    public ResponseEntity<?> loadDatabaseData(@Valid @RequestBody FilenameDto filenameDto) {
        try {
            dataService.loadDatabaseData(filenameDto.getFilename());

            return ResponseEntity.ok().build();
        } catch (IOException ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }
}
