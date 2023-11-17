package com.example.moviemetrics.api.exception;

public class DataConflictException extends RuntimeException {

    public DataConflictException() {
        super("Conflict");
    }

    public DataConflictException(String message) {
        super(message);
    }
}
