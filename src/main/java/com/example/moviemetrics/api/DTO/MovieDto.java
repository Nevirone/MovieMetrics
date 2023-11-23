package com.example.moviemetrics.api.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class MovieDto {
    @NotNull(message = "Title is required")
    @Size(min = 5, message = "At least 5 characters")
    private String title;

    @NotNull(message = "Description is required")
    @Size(min = 10, message = "At least 10 characters")
    private String description;

    @NotNull(message = "Genres is required")
    private HashSet<String> genres;

    @NotNull(message = "Popularity is required")
    private double popularity;

    @NotNull(message = "VoteAverage is required")
    private double voteAverage;

    @NotNull(message = "VoteCount is required")
    private int voteCount;
}
