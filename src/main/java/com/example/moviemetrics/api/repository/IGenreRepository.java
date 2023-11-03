package com.example.moviemetrics.api.repository;

import com.example.moviemetrics.api.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IGenreRepository extends JpaRepository<Genre, Long> {

    Optional<Genre> findByName(String name);
}
