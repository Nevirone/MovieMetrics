package com.example.moviemetrics.api.request;

import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.model.Movie;

import java.util.HashSet;
import java.util.Set;

public class MovieRequest {
    private String title;
    private String description;
    private double popularity;
    private double voteAverage;
    private int voteCount;
    private Set<Long> genreIds;

    public Movie getMovie() {
        return new Movie(
                this.title,
                this.description,
                this.popularity,
                this.voteAverage,
                this.getVoteCount()
        );
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public Set<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(Set<Long> genreIds) {
        this.genreIds = genreIds;
    }
}
