package com.example.moviemetrics.api.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.nio.file.AccessDeniedException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, List<String>> body = new HashMap<>();
        List<String> errors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage()));

        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(DataConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<String> handleDataConflictException(DataConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<?> accessDeniedException(org.springframework.security.access.AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
    }

    @ExceptionHandler(org.springframework.security.access.AuthorizationServiceException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?> accessDeniedException(org.springframework.security.access.AuthorizationServiceException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }
}
