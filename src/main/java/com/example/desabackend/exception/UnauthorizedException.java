package com.example.desabackend.exception;

/**
 * Domain exception used to map invalid credentials to HTTP 401.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
