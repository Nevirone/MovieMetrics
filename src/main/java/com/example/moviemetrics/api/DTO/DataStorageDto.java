package com.example.moviemetrics.api.DTO;

import com.example.moviemetrics.api.model.ERole;
import com.example.moviemetrics.api.model.Genre;
import com.example.moviemetrics.api.model.Movie;
import com.example.moviemetrics.api.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
public class DataStorageDto {
    @JsonIgnore
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private List<GenreDto> genreDtos;
    private List<MovieDto> movieDtos;
    private List<UserDto> userDtos;

    public DataStorageDto(List<Genre> genres, List<Movie> movies, List<User> users) {
        genreDtos = new ArrayList<>();
        for(Genre g : genres) {
            GenreDto dto = GenreDto
                    .builder()
                    .name(g.getName())
                    .build();
            genreDtos.add(dto);
        }

        movieDtos = new ArrayList<>();
        for(Movie m : movies) {
            Set<String> titles = m.getGenres().stream().map(Genre::getName).collect(Collectors.toSet());

            MovieDto dto = MovieDto
                    .builder()
                    .title(m.getTitle())
                    .description(m.getDescription())
                    .genres(new HashSet<>(titles))
                    .voteAverage(m.getVoteAverage())
                    .voteCount(m.getVoteCount())
                    .popularity(m.getPopularity())
                    .build();

            movieDtos.add(dto);
        }

        userDtos = new ArrayList<>();
        for(User u : users) {
            UserDto dto = UserDto
                    .builder()
                    .email(u.getEmail())
                    .password(u.getPassword())
                    .isPasswordEncrypted(true)
                    .isAdmin(u.getERole() == ERole.ADMIN)
                    .build();

            userDtos.add(dto);
        }
    }

    public static DataStorageDto readFromJsonString(String json) throws JsonProcessingException{
        return objectMapper.readValue(json, DataStorageDto.class);
    }

    public String toJsonString() throws JsonProcessingException {
        return objectMapper.writeValueAsString(this);
    }

}
