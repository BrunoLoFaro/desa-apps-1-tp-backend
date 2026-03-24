package com.example.desabackend.exception;

/**
 * Simple domain exception used to map not-found resources to HTTP 404.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
