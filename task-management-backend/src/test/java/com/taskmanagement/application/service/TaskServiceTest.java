package com.taskmanagement.application.service;

import com.taskmanagement.application.dto.request.CreateTaskRequest;
import com.taskmanagement.application.dto.request.UpdateTaskRequest;
import com.taskmanagement.application.dto.response.TaskResponse;
import com.taskmanagement.domain.entity.Project;
import com.taskmanagement.domain.entity.Task;
import com.taskmanagement.domain.entity.User;
import com.taskmanagement.domain.repository.ProjectRepository;
import com.taskmanagement.domain.repository.TaskRepository;
import com.taskmanagement.domain.repository.UserRepository;
import com.taskmanagement.infrastructure.exception.ResourceNotFoundException;
import com.taskmanagement.infrastructure.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskService.
 * Tests task CRUD operations and authorization.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Task Service Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Project project;
    private Task task;
    private CreateTaskRequest createRequest;
    private UpdateTaskRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Setup security context
        SecurityContextHolder.setContext(securityContext);

        // Setup test data
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .role(User.Role.USER)
                .enabled(true)
                .build();

        project = Project.builder()
                .id(1L)
                .title("Test Project")
                .description("Test Description")
                .owner(user)
                .build();

        task = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .completed(false)
                .dueDate(LocalDate.now().plusDays(7))
                .project(project)
                .build();

        createRequest = CreateTaskRequest.builder()
                .title("New Task")
                .description("New Description")
                .dueDate(LocalDate.now().plusDays(5))
                .build();

        updateRequest = UpdateTaskRequest.builder()
                .title("Updated Task")
                .description("Updated Description")
                .dueDate(LocalDate.now().plusDays(10))
                .completed(true)
                .build();
    }

    @Test
    @DisplayName("Should create task successfully")
    void shouldCreateTaskSuccessfully() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.existsByIdAndOwnerId(1L, user.getId())).thenReturn(true);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // When
        TaskResponse response = taskService.createTask(1L, createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(task.getTitle());
        assertThat(response.getProjectId()).isEqualTo(project.getId());

        verify(projectRepository).existsByIdAndOwnerId(1L, user.getId());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when user doesn't own project")
    void shouldThrowExceptionWhenUserDoesntOwnProject() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.existsByIdAndOwnerId(1L, user.getId())).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> taskService.createTask(1L, createRequest))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("don't have access");

        verify(projectRepository).existsByIdAndOwnerId(1L, user.getId());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("Should get all tasks in project")
    void shouldGetAllTasksInProject() {
        // Given
        Task task2 = Task.builder()
                .id(2L)
                .title("Task 2")
                .description("Description 2")
                .completed(false)
                .project(project)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.existsByIdAndOwnerId(1L, user.getId())).thenReturn(true);
        when(taskRepository.findByProjectIdOrderByCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(task, task2));

        // When
        List<TaskResponse> responses = taskService.getAllTasksInProject(1L);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getTitle()).isEqualTo(task.getTitle());
        assertThat(responses.get(1).getTitle()).isEqualTo(task2.getTitle());

        verify(taskRepository).findByProjectIdOrderByCreatedAtDesc(1L);
    }

    @Test
    @DisplayName("Should get task by ID")
    void shouldGetTaskById() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.existsByIdAndOwnerId(1L, user.getId())).thenReturn(true);
        when(taskRepository.findByIdAndProjectId(1L, 1L)).thenReturn(Optional.of(task));

        // When
        TaskResponse response = taskService.getTaskById(1L, 1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo(task.getTitle());

        verify(taskRepository).findByIdAndProjectId(1L, 1L);
    }

    @Test
    @DisplayName("Should throw exception when task not found")
    void shouldThrowExceptionWhenTaskNotFound() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.existsByIdAndOwnerId(1L, user.getId())).thenReturn(true);
        when(taskRepository.findByIdAndProjectId(999L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> taskService.getTaskById(1L, 999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(taskRepository).findByIdAndProjectId(999L, 1L);
    }

    @Test
    @DisplayName("Should update task successfully")
    void shouldUpdateTaskSuccessfully() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.existsByIdAndOwnerId(1L, user.getId())).thenReturn(true);
        when(taskRepository.findByIdAndProjectId(1L, 1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // When
        TaskResponse response = taskService.updateTask(1L, 1L, updateRequest);

        // Then
        assertThat(response).isNotNull();
        verify(taskRepository).findByIdAndProjectId(1L, 1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Should mark task as completed")
    void shouldMarkTaskAsCompleted() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.existsByIdAndOwnerId(1L, user.getId())).thenReturn(true);
        when(taskRepository.findByIdAndProjectId(1L, 1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // When
        TaskResponse response = taskService.markTaskAsCompleted(1L, 1L);

        // Then
        assertThat(response).isNotNull();
        verify(taskRepository).findByIdAndProjectId(1L, 1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Should delete task successfully")
    void shouldDeleteTaskSuccessfully() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.existsByIdAndOwnerId(1L, user.getId())).thenReturn(true);
        when(taskRepository.findByIdAndProjectId(1L, 1L)).thenReturn(Optional.of(task));

        // When
        taskService.deleteTask(1L, 1L);

        // Then
        verify(taskRepository).findByIdAndProjectId(1L, 1L);
        verify(taskRepository).delete(task);
    }

    @Test
    @DisplayName("Should throw exception when user not authenticated")
    void shouldThrowExceptionWhenNotAuthenticated() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> taskService.getAllTasksInProject(1L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("not authenticated");
    }

    @Test
    @DisplayName("Should update task with only description")
    void shouldUpdateTaskWithOnlyDescription() {
        // Given
        UpdateTaskRequest partialUpdate = UpdateTaskRequest.builder()
                .title(null)
                .description("Only Description Updated")
                .dueDate(null)
                .completed(null)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.existsByIdAndOwnerId(1L, user.getId())).thenReturn(true);
        when(taskRepository.findByIdAndProjectId(1L, 1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // When
        TaskResponse response = taskService.updateTask(1L, 1L, partialUpdate);

        // Then
        assertThat(response).isNotNull();
        verify(taskRepository).save(any(Task.class));
    }
    @Test
    @DisplayName("Should update task with only due date")
    void shouldUpdateTaskWithOnlyDueDate() {
        // Given
        UpdateTaskRequest partialUpdate = UpdateTaskRequest.builder()
                .title(null)
                .description(null)
                .dueDate(LocalDate.now().plusDays(15))
                .completed(null)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.existsByIdAndOwnerId(1L, user.getId())).thenReturn(true);
        when(taskRepository.findByIdAndProjectId(1L, 1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // When
        TaskResponse response = taskService.updateTask(1L, 1L, partialUpdate);

        // Then
        assertThat(response).isNotNull();
        verify(taskRepository).save(any(Task.class));
    }
}