package com.example.moviemetrics.api.model;

import com.example.moviemetrics.api.service.GenreService;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor @RequiredArgsConstructor
@Table(name = "movies")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String title;

    @NonNull
    private String description;

    @NonNull
    private double popularity;

    @NonNull
    @Column(name = "vote_average")
    private double voteAverage;

    @NonNull
    @Column(name = "vote_count")
    private int voteCount;

    @NonNull
    @JsonManagedReference
    @ManyToMany
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

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
