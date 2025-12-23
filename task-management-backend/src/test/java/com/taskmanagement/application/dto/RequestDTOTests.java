package com.taskmanagement.application.dto;

import com.taskmanagement.application.dto.request.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for Request DTO validation.
 */
@DisplayName("Request DTO Tests")
class RequestDTOTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should validate RegisterRequest successfully")
    void shouldValidateRegisterRequest() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("Test@1234")
                .build();

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation for RegisterRequest with invalid email")
    void shouldFailValidationForInvalidEmail() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .name("Test User")
                .email("invalid-email")
                .password("Test@1234")
                .build();

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should fail validation for RegisterRequest with empty name")
    void shouldFailValidationForEmptyName() {
        // Given
        RegisterRequest request = RegisterRequest.builder()
                .name("")
                .email("test@example.com")
                .password("Test@1234")
                .build();

        // When
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should validate LoginRequest successfully")
    void shouldValidateLoginRequest() {
        // Given
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("Test@1234")
                .build();

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should validate CreateProjectRequest successfully")
    void shouldValidateCreateProjectRequest() {
        // Given
        CreateProjectRequest request = CreateProjectRequest.builder()
                .title("Test Project")
                .description("Test Description")
                .build();

        // When
        Set<ConstraintViolation<CreateProjectRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation for CreateProjectRequest with empty title")
    void shouldFailValidationForEmptyTitle() {
        // Given
        CreateProjectRequest request = CreateProjectRequest.builder()
                .title("")
                .description("Test Description")
                .build();

        // When
        Set<ConstraintViolation<CreateProjectRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should validate UpdateProjectRequest successfully")
    void shouldValidateUpdateProjectRequest() {
        // Given
        UpdateProjectRequest request = UpdateProjectRequest.builder()
                .title("Updated Project")
                .description("Updated Description")
                .build();

        // When
        Set<ConstraintViolation<UpdateProjectRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should validate CreateTaskRequest successfully")
    void shouldValidateCreateTaskRequest() {
        // Given
        CreateTaskRequest request = CreateTaskRequest.builder()
                .title("Test Task")
                .description("Test Description")
                .dueDate(LocalDate.now().plusDays(7))
                .build();

        // When
        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation for CreateTaskRequest with empty title")
    void shouldFailValidationForTaskEmptyTitle() {
        // Given
        CreateTaskRequest request = CreateTaskRequest.builder()
                .title("")
                .description("Test Description")
                .dueDate(LocalDate.now().plusDays(7))
                .build();

        // When
        Set<ConstraintViolation<CreateTaskRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Should validate UpdateTaskRequest successfully")
    void shouldValidateUpdateTaskRequest() {
        // Given
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .title("Updated Task")
                .description("Updated Description")
                .dueDate(LocalDate.now().plusDays(10))
                .completed(true)
                .build();

        // When
        Set<ConstraintViolation<UpdateTaskRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should test builder and getters for all request DTOs")
    void shouldTestBuildersAndGetters() {
        // RegisterRequest
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Test")
                .email("test@example.com")
                .password("password")
                .build();
        assertThat(registerRequest.getName()).isEqualTo("Test");
        assertThat(registerRequest.getEmail()).isEqualTo("test@example.com");
        assertThat(registerRequest.getPassword()).isEqualTo("password");

        // LoginRequest
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();
        assertThat(loginRequest.getEmail()).isEqualTo("test@example.com");
        assertThat(loginRequest.getPassword()).isEqualTo("password");

        // CreateProjectRequest
        CreateProjectRequest createProjectRequest = CreateProjectRequest.builder()
                .title("Title")
                .description("Description")
                .build();
        assertThat(createProjectRequest.getTitle()).isEqualTo("Title");
        assertThat(createProjectRequest.getDescription()).isEqualTo("Description");
    }
}