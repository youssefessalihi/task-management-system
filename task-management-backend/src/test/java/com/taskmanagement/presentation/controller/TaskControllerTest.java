package com.taskmanagement.presentation.controller;

import com.taskmanagement.application.dto.request.CreateTaskRequest;
import com.taskmanagement.application.dto.request.UpdateTaskRequest;
import com.taskmanagement.application.dto.response.ApiResponse;
import com.taskmanagement.application.dto.response.TaskResponse;
import com.taskmanagement.application.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for TaskController.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Task Controller Tests")
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private CreateTaskRequest createRequest;
    private UpdateTaskRequest updateRequest;
    private TaskResponse taskResponse;

    @BeforeEach
    void setUp() {
        createRequest = CreateTaskRequest.builder()
                .title("Test Task")
                .description("Test Description")
                .dueDate(LocalDate.now().plusDays(7))
                .build();

        updateRequest = UpdateTaskRequest.builder()
                .title("Updated Task")
                .description("Updated Description")
                .dueDate(LocalDate.now().plusDays(10))
                .completed(true)
                .build();

        taskResponse = TaskResponse.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .completed(false)
                .dueDate(LocalDate.now().plusDays(7))
                .overdue(false)
                .projectId(1L)
                .projectTitle("Test Project")
                .build();
    }

    @Test
    @DisplayName("Should create task successfully")
    void shouldCreateTask() {
        // Given
        when(taskService.createTask(1L, createRequest)).thenReturn(taskResponse);

        // When
        ResponseEntity<ApiResponse<TaskResponse>> response =
                taskController.createTask(1L, createRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEqualTo(taskResponse);
        assertThat(response.getBody().getMessage()).isEqualTo("Task created successfully");

        verify(taskService).createTask(1L, createRequest);
    }

    @Test
    @DisplayName("Should get all tasks successfully")
    void shouldGetAllTasks() {
        // Given
        List<TaskResponse> tasks = Arrays.asList(taskResponse);
        when(taskService.getAllTasksInProject(1L)).thenReturn(tasks);

        // When
        ResponseEntity<ApiResponse<List<TaskResponse>>> response =
                taskController.getAllTasks(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEqualTo(tasks);

        verify(taskService).getAllTasksInProject(1L);
    }

    @Test
    @DisplayName("Should get task by ID successfully")
    void shouldGetTaskById() {
        // Given
        when(taskService.getTaskById(1L, 1L)).thenReturn(taskResponse);

        // When
        ResponseEntity<ApiResponse<TaskResponse>> response =
                taskController.getTaskById(1L, 1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEqualTo(taskResponse);

        verify(taskService).getTaskById(1L, 1L);
    }

    @Test
    @DisplayName("Should update task successfully")
    void shouldUpdateTask() {
        // Given
        when(taskService.updateTask(1L, 1L, updateRequest)).thenReturn(taskResponse);

        // When
        ResponseEntity<ApiResponse<TaskResponse>> response =
                taskController.updateTask(1L, 1L, updateRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEqualTo(taskResponse);
        assertThat(response.getBody().getMessage()).isEqualTo("Task updated successfully");

        verify(taskService).updateTask(1L, 1L, updateRequest);
    }

    @Test
    @DisplayName("Should mark task as completed successfully")
    void shouldMarkTaskAsCompleted() {
        // Given
        when(taskService.markTaskAsCompleted(1L, 1L)).thenReturn(taskResponse);

        // When
        ResponseEntity<ApiResponse<TaskResponse>> response =
                taskController.markTaskAsCompleted(1L, 1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEqualTo(taskResponse);
        assertThat(response.getBody().getMessage()).isEqualTo("Task marked as completed");

        verify(taskService).markTaskAsCompleted(1L, 1L);
    }

    @Test
    @DisplayName("Should delete task successfully")
    void shouldDeleteTask() {
        // When
        ResponseEntity<ApiResponse<Void>> response =
                taskController.deleteTask(1L, 1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Task deleted successfully");

        verify(taskService).deleteTask(1L, 1L);
    }
}