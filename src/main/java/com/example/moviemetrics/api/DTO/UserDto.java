package com.example.moviemetrics.api.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class UserDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "At least 6 characters")
    private String password;

    @NotBlank(message = "IsPasswordEncrypted is required")
    private boolean isPasswordEncrypted;

    @NotBlank(message = "IsAdmin is required")
    private boolean isAdmin;
}
