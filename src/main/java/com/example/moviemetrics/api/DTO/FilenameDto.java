package com.example.moviemetrics.api.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class FilenameDto {

    @NotNull(message = "Filename is required")
    private String filename;
}
