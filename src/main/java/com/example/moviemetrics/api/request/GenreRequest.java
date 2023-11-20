package com.example.moviemetrics.api.request;

import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.model.Movie;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class GenreRequest {
    @NotNull(message = "Name is required")
    @Size(min = 5, message = "At least 5 characters")
    private String name;

    public Genre getGenre() {
        return new Genre(
                this.name
        );
    }
}
