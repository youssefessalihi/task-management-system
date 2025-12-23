package com.taskmanagement.infrastructure.exception;

/**
 * Exception thrown when request data is invalid.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}