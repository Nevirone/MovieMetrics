package com.example.moviemetrics.api.request;

import com.example.moviemetrics.api.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class UserRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invaild email")
    private String email;

    @NotBlank(message = "Username is required")
    @Size(min = 5, message = "At least 5 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "At least 6 characters")
    private String password;

    public User getUser() {
        return new User(
            this.email,
            this.username,
            this.password
        );
    }
}
