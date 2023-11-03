package com.example.moviemetrics.api.exception;

public class MovieTitleTakenException extends RuntimeException {

    public MovieTitleTakenException() {
        super("Movie title already taken.");
    }

    public MovieTitleTakenException(String message) {
        super(message);
    }
}
