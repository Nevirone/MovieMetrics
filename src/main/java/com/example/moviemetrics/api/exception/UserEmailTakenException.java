package com.example.moviemetrics.api.exception;

public class UserEmailTakenException extends RuntimeException {

    public UserEmailTakenException() {
        super("Email already taken.");
    }

    public UserEmailTakenException(String message) {
        super(message);
    }
}
