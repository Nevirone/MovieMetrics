package com.example.moviemetrics.api.request;

import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.model.Movie;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenreRequest {
    @NotNull(message = "Name is required")
    @Min(value = 5, message = "At least 5 characters")
    private String name;

    public Genre getGenre() {
        return new Genre(
                this.name
        );
    }
}
