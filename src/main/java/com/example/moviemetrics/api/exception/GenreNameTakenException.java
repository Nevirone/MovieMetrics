package com.example.moviemetrics.api.exception;

public class GenreNameTakenException extends RuntimeException {

    public GenreNameTakenException() {
        super("Genre name already taken.");
    }

    public GenreNameTakenException(String message) {
        super(message);
    }
}
