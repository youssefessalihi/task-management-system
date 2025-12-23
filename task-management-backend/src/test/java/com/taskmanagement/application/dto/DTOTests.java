package com.taskmanagement.application.dto;

import com.taskmanagement.application.dto.response.*;
import com.taskmanagement.domain.entity.Project;
import com.taskmanagement.domain.entity.Task;
import com.taskmanagement.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for DTO classes.
 */
@DisplayName("DTO Tests")
class DTOTests {

    @Test
    @DisplayName("Should create UserResponse from User entity")
    void shouldCreateUserResponseFromEntity() {
        // Given
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .role(User.Role.USER)
                .enabled(true)
                .build();

        // When
        UserResponse response = UserResponse.fromEntity(user);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Test User");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getRole()).isEqualTo("USER");
        assertThat(response.getEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should create ProjectResponse from Project entity")
    void shouldCreateProjectResponseFromEntity() {
        // Given
        User owner = User.builder()
                .id(1L)
                .name("Owner")
                .email("owner@example.com")
                .build();

        Project project = Project.builder()
                .id(1L)
                .title("Test Project")
                .description("Description")
                .owner(owner)
                .build();

        // When
        ProjectResponse response = ProjectResponse.fromEntity(project);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test Project");
        assertThat(response.getOwnerId()).isEqualTo(1L);
        assertThat(response.getOwnerName()).isEqualTo("Owner");
    }

    @Test
    @DisplayName("Should create ProjectResponse with task counts")
    void shouldCreateProjectResponseWithTaskCounts() {
        // Given
        User owner = User.builder()
                .id(1L)
                .name("Owner")
                .build();

        Project project = Project.builder()
                .id(1L)
                .title("Test Project")
                .owner(owner)
                .build();

        // When
        ProjectResponse response = ProjectResponse.fromEntity(project, 10, 6);

        // Then
        assertThat(response.getTotalTasks()).isEqualTo(10);
        assertThat(response.getCompletedTasks()).isEqualTo(6);
        assertThat(response.getProgressPercentage()).isEqualTo(60.0);
    }

    @Test
    @DisplayName("Should create TaskResponse from Task entity")
    void shouldCreateTaskResponseFromEntity() {
        // Given
        Project project = Project.builder()
                .id(1L)
                .title("Test Project")
                .build();

        Task task = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Description")
                .completed(false)
                .dueDate(LocalDate.now().plusDays(7))
                .project(project)
                .build();

        // When
        TaskResponse response = TaskResponse.fromEntity(task);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Test Task");
        assertThat(response.getProjectId()).isEqualTo(1L);
        assertThat(response.getProjectTitle()).isEqualTo("Test Project");
        assertThat(response.getCompleted()).isFalse();
        assertThat(response.getOverdue()).isFalse();
    }

    @Test
    @DisplayName("Should create ProjectProgressResponse")
    void shouldCreateProjectProgressResponse() {
        // When
        ProjectProgressResponse response = ProjectProgressResponse.of(1L, "Test Project", 10, 7);

        // Then
        assertThat(response.getProjectId()).isEqualTo(1L);
        assertThat(response.getProjectTitle()).isEqualTo("Test Project");
        assertThat(response.getTotalTasks()).isEqualTo(10);
        assertThat(response.getCompletedTasks()).isEqualTo(7);
        assertThat(response.getIncompleteTasks()).isEqualTo(3);
        assertThat(response.getProgressPercentage()).isEqualTo(70.0);
    }

    @Test
    @DisplayName("Should create AuthResponse")
    void shouldCreateAuthResponse() {
        // Given
        UserResponse user = UserResponse.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        // When
        AuthResponse response = AuthResponse.of("test-token", user);

        // Then
        assertThat(response.getToken()).isEqualTo("test-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Should create ApiResponse success with message only")
    void shouldCreateApiResponseSuccessWithMessageOnly() {
        // When
        ApiResponse<Void> response = ApiResponse.success("Success message");

        // Then
        assertThat(response.getSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Success message");
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should create ApiResponse success with message")
    void shouldCreateApiResponseSuccessWithMessage() {
        // When
        ApiResponse<String> response = ApiResponse.success("Success message", "Test data");

        // Then
        assertThat(response.getSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Success message");
        assertThat(response.getData()).isEqualTo("Test data");
    }

    @Test
    @DisplayName("Should create ApiResponse error")
    void shouldCreateApiResponseError() {
        // When
        ApiResponse<Void> response = ApiResponse.error("Error message");

        // Then
        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Error message");
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should handle null owner in ProjectResponse")
    void shouldHandleNullOwnerInProjectResponse() {
        // Given
        Project project = Project.builder()
                .id(1L)
                .title("Test Project")
                .owner(null) // Null owner
                .build();

        // When
        ProjectResponse response = ProjectResponse.fromEntity(project);

        // Then
        assertThat(response.getOwnerId()).isNull();
        assertThat(response.getOwnerName()).isEqualTo("Unknown");
    }

    @Test
    @DisplayName("Should handle null project in TaskResponse")
    void shouldHandleNullProjectInTaskResponse() {
        // Given
        Task task = Task.builder()
                .id(1L)
                .title("Test Task")
                .project(null) // Null project
                .build();

        // When
        TaskResponse response = TaskResponse.fromEntity(task);

        // Then
        assertThat(response.getProjectId()).isNull();
        assertThat(response.getProjectTitle()).isEqualTo("Unknown");
    }
}