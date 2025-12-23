package com.taskmanagement.application.service;

import com.taskmanagement.application.dto.request.CreateProjectRequest;
import com.taskmanagement.application.dto.request.UpdateProjectRequest;
import com.taskmanagement.application.dto.response.ProjectProgressResponse;
import com.taskmanagement.application.dto.response.ProjectResponse;
import com.taskmanagement.domain.entity.Project;
import com.taskmanagement.domain.entity.User;
import com.taskmanagement.domain.repository.ProjectRepository;
import com.taskmanagement.domain.repository.TaskRepository;
import com.taskmanagement.domain.repository.UserRepository;
import com.taskmanagement.infrastructure.exception.ResourceNotFoundException;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProjectService.
 * Tests project CRUD operations and authorization.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Project Service Tests")
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProjectService projectService;

    private User user;
    private Project project;
    private CreateProjectRequest createRequest;
    private UpdateProjectRequest updateRequest;

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

        createRequest = CreateProjectRequest.builder()
                .title("New Project")
                .description("New Description")
                .build();

        updateRequest = UpdateProjectRequest.builder()
                .title("Updated Project")
                .description("Updated Description")
                .build();
    }

    @Test
    @DisplayName("Should create project successfully")
    void shouldCreateProjectSuccessfully() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        // When
        ProjectResponse response = projectService.createProject(createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(project.getTitle());
        assertThat(response.getOwnerId()).isEqualTo(user.getId());

        verify(userRepository).findByEmail("test@example.com");
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    @DisplayName("Should get all projects for current user")
    void shouldGetAllProjects() {
        // Given
        Project project2 = Project.builder()
                .id(2L)
                .title("Project 2")
                .description("Description 2")
                .owner(user)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.findByOwnerIdOrderByCreatedAtDesc(user.getId()))
                .thenReturn(Arrays.asList(project, project2));
        when(taskRepository.countByProjectId(anyLong())).thenReturn(5L);
        when(taskRepository.countByProjectIdAndCompletedTrue(anyLong())).thenReturn(3L);

        // When
        List<ProjectResponse> responses = projectService.getAllProjects();

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getTitle()).isEqualTo(project.getTitle());
        assertThat(responses.get(1).getTitle()).isEqualTo(project2.getTitle());

        verify(userRepository).findByEmail("test@example.com");
        verify(projectRepository).findByOwnerIdOrderByCreatedAtDesc(user.getId());
    }

    @Test
    @DisplayName("Should get project by ID")
    void shouldGetProjectById() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.findByIdAndOwnerId(1L, user.getId())).thenReturn(Optional.of(project));
        when(taskRepository.countByProjectId(1L)).thenReturn(10L);
        when(taskRepository.countByProjectIdAndCompletedTrue(1L)).thenReturn(7L);

        // When
        ProjectResponse response = projectService.getProjectById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo(project.getTitle());
        assertThat(response.getTotalTasks()).isEqualTo(10);
        assertThat(response.getCompletedTasks()).isEqualTo(7);

        verify(projectRepository).findByIdAndOwnerId(1L, user.getId());
    }

    @Test
    @DisplayName("Should throw exception when project not found")
    void shouldThrowExceptionWhenProjectNotFound() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.findByIdAndOwnerId(999L, user.getId())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.getProjectById(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(projectRepository).findByIdAndOwnerId(999L, user.getId());
    }

    @Test
    @DisplayName("Should update project successfully")
    void shouldUpdateProjectSuccessfully() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.findByIdAndOwnerId(1L, user.getId())).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(taskRepository.countByProjectId(1L)).thenReturn(5L);
        when(taskRepository.countByProjectIdAndCompletedTrue(1L)).thenReturn(2L);

        // When
        ProjectResponse response = projectService.updateProject(1L, updateRequest);

        // Then
        assertThat(response).isNotNull();
        verify(projectRepository).findByIdAndOwnerId(1L, user.getId());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    @DisplayName("Should delete project successfully")
    void shouldDeleteProjectSuccessfully() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.findByIdAndOwnerId(1L, user.getId())).thenReturn(Optional.of(project));

        // When
        projectService.deleteProject(1L);

        // Then
        verify(projectRepository).findByIdAndOwnerId(1L, user.getId());
        verify(projectRepository).delete(project);
    }

    @Test
    @DisplayName("Should get project progress")
    void shouldGetProjectProgress() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.findByIdAndOwnerId(1L, user.getId())).thenReturn(Optional.of(project));
        when(taskRepository.countByProjectId(1L)).thenReturn(10L);
        when(taskRepository.countByProjectIdAndCompletedTrue(1L)).thenReturn(6L);

        // When
        ProjectProgressResponse response = projectService.getProjectProgress(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getProjectId()).isEqualTo(1L);
        assertThat(response.getTotalTasks()).isEqualTo(10);
        assertThat(response.getCompletedTasks()).isEqualTo(6);
        assertThat(response.getProgressPercentage()).isEqualTo(60.0);

        verify(projectRepository).findByIdAndOwnerId(1L, user.getId());
    }

    @Test
    @DisplayName("Should throw exception when user not authenticated")
    void shouldThrowExceptionWhenNotAuthenticated() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> projectService.getAllProjects())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Unauthenticated user");
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.getAllProjects())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should update project with both fields null (no changes)")
    void shouldUpdateProjectWithNoChanges() {
        // Given
        UpdateProjectRequest emptyUpdate = UpdateProjectRequest.builder()
                .title(null)
                .description(null)
                .build();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.findByIdAndOwnerId(1L, user.getId())).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(taskRepository.countByProjectId(1L)).thenReturn(5L);
        when(taskRepository.countByProjectIdAndCompletedTrue(1L)).thenReturn(3L);

        // When
        ProjectResponse response = projectService.updateProject(1L, emptyUpdate);

        // Then
        assertThat(response).isNotNull();
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    @DisplayName("Should create project and fetch with fromEntity method")
    void shouldCreateProjectWithBasicFromEntity() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Create a project with tasks to test fromEntity with task calculation
        Project projectWithTasks = Project.builder()
                .id(1L)
                .title("Test Project")
                .description("Test Description")
                .owner(user)
                .build();

        when(projectRepository.save(any(Project.class))).thenReturn(projectWithTasks);

        // When
        ProjectResponse response = projectService.createProject(createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getProgressPercentage()).isNotNull();
    }

    @Test
    @DisplayName("Should get project with 100% progress")
    void shouldGetProjectWith100PercentProgress() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(projectRepository.findByIdAndOwnerId(1L, user.getId())).thenReturn(Optional.of(project));
        when(taskRepository.countByProjectId(1L)).thenReturn(10L);
        when(taskRepository.countByProjectIdAndCompletedTrue(1L)).thenReturn(10L); // All completed

        // When
        ProjectProgressResponse response = projectService.getProjectProgress(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getProgressPercentage()).isEqualTo(100.0);
    }
}