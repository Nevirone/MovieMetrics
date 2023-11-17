package com.example.moviemetrics.api.repository;

import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IGenreRepository extends JpaRepository<Genre, Long> {
    @Query("SELECT g FROM Genre g WHERE g.name = :name")
    Optional<Genre> findByName(@Param("name") String name);
}
