package com.taskmanagement.infrastructure.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for custom exception classes.
 */
@DisplayName("Exception Tests")
class ExceptionTests {

    @Test
    @DisplayName("Should create ResourceNotFoundException with message")
    void shouldCreateResourceNotFoundExceptionWithMessage() {
        // When
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        // Then
        assertThat(exception.getMessage()).isEqualTo("Resource not found");
    }

    @Test
    @DisplayName("Should create ResourceNotFoundException with parameters")
    void shouldCreateResourceNotFoundExceptionWithParameters() {
        // When
        ResourceNotFoundException exception = new ResourceNotFoundException("User", "id", 123L);

        // Then
        assertThat(exception.getMessage()).isEqualTo("User not found with id: '123'");
    }

    @Test
    @DisplayName("Should create UnauthorizedException")
    void shouldCreateUnauthorizedException() {
        // When
        UnauthorizedException exception = new UnauthorizedException("Unauthorized access");

        // Then
        assertThat(exception.getMessage()).isEqualTo("Unauthorized access");
    }

    @Test
    @DisplayName("Should create BadRequestException")
    void shouldCreateBadRequestException() {
        // When
        BadRequestException exception = new BadRequestException("Bad request");

        // Then
        assertThat(exception.getMessage()).isEqualTo("Bad request");
    }
}