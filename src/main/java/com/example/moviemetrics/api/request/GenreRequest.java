package com.example.moviemetrics.api.request;

import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.model.Movie;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class GenreRequest {
    @NotNull(message = "Name is required")
    @Size(min = 5, message = "At least 5 characters")
    private String name;
}
