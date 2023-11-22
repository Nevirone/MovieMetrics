package com.example.moviemetrics.api.request;

import com.example.moviemetrics.api.model.Movie;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class MovieRequest {
    @NotNull(message = "Title is required")
    @Size(min = 5, message = "At least 5 characters")
    private String title;

    @NotNull(message = "Description is required")
    @Size(min = 10, message = "At least 10 characters")
    private String description;

    @NotNull(message = "Popularity is required")
    private double popularity;

    @NotNull(message = "VoteAverage is required")
    private double voteAverage;

    @NotNull(message = "VoteCount is required")
    private int voteCount;

    @NotNull(message = "GenreIds is required")
    private Set<Long> genreIds;
}
