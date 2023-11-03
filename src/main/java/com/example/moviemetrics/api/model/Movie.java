package com.example.moviemetrics.api.model;

import com.example.moviemetrics.api.exception.GenreNotFoundException;
import com.example.moviemetrics.api.service.GenreService;
import jakarta.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private double popularity;

    @Column(name = "vote_average")
    private double voteAverage;

    @Column(name = "vote_count")
    private int voteCount;

    @ManyToMany
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    public Movie() {}

    public Movie(String title, String description, double popularity, double voteAverage, int voteCount) {
        this.title = title;
        this.description = description;
        this.popularity = popularity;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public void setGenresByIds(Set<Long> genreIds, GenreService genreService) {
        if(genreIds == null) return;

        Set<Genre> genres = new HashSet<>();

        for (Long genreId : genreIds) {
            Genre genre = genreService.getGenreById(genreId);
            genres.add(genre);
        }

        this.genres = genres;
    }
}
