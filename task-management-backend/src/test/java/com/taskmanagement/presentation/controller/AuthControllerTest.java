package com.taskmanagement.presentation.controller;

import com.taskmanagement.application.dto.request.LoginRequest;
import com.taskmanagement.application.dto.request.RegisterRequest;
import com.taskmanagement.application.dto.response.ApiResponse;
import com.taskmanagement.application.dto.response.AuthResponse;
import com.taskmanagement.application.dto.response.UserResponse;
import com.taskmanagement.application.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AuthController.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Controller Tests")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("Test@1234")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("Test@1234")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .build();

        authResponse = AuthResponse.of("test-token", userResponse);
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUser() {
        // Given
        when(authService.register(registerRequest)).thenReturn(authResponse);

        // When
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.register(registerRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEqualTo(authResponse);
        assertThat(response.getBody().getMessage()).isEqualTo("User registered successfully");

        verify(authService).register(registerRequest);
    }

    @Test
    @DisplayName("Should login user successfully")
    void shouldLoginUser() {
        // Given
        when(authService.login(loginRequest)).thenReturn(authResponse);

        // When
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(loginRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEqualTo(authResponse);
        assertThat(response.getBody().getMessage()).isEqualTo("Login successful");

        verify(authService).login(loginRequest);
    }
}