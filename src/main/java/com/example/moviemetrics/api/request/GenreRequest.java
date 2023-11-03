package com.example.moviemetrics.api.request;

import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.model.Movie;

public class GenreRequest {
    private String name;

    public Genre getGenre() {
        return new Genre(
                this.name
        );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
