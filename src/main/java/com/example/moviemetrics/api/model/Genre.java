package com.example.moviemetrics.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "genres")
public class Genre {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @JsonBackReference
    @ManyToMany(mappedBy = "genres", cascade = CascadeType.REMOVE)
    private Set<Movie> movies = new HashSet<>();
}
