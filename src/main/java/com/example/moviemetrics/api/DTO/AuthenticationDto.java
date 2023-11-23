package com.example.moviemetrics.api.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class AuthenticationDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    private String password;
}
