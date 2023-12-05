package com.example.moviemetrics.api.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class MovieComparisonDto {
    private String comparedMovieTitle;
    private String comparedToMovieTitle;

    private int voteCountDifference;
    private double voteAverageDifference;
    private double popularityDifference;
}
