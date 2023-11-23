package com.example.moviemetrics.api.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class GenreDto {
    @NotNull(message = "Name is required")
    @Size(min = 5, message = "At least 5 characters")
    private String name;
}
