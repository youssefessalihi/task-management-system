package com.taskmanagement.presentation.controller;

import com.taskmanagement.application.dto.request.CreateProjectRequest;
import com.taskmanagement.application.dto.request.UpdateProjectRequest;
import com.taskmanagement.application.dto.response.ApiResponse;
import com.taskmanagement.application.dto.response.ProjectProgressResponse;
import com.taskmanagement.application.dto.response.ProjectResponse;
import com.taskmanagement.application.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ProjectController.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Project Controller Tests")
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    private CreateProjectRequest createRequest;
    private UpdateProjectRequest updateRequest;
    private ProjectResponse projectResponse;
    private ProjectProgressResponse progressResponse;

    @BeforeEach
    void setUp() {
        createRequest = CreateProjectRequest.builder()
                .title("Test Project")
                .description("Test Description")
                .build();

        updateRequest = UpdateProjectRequest.builder()
                .title("Updated Project")
                .description("Updated Description")
                .build();

        projectResponse = ProjectResponse.builder()
                .id(1L)
                .title("Test Project")
                .description("Test Description")
                .ownerId(1L)
                .ownerName("Test Owner")
                .totalTasks(10)
                .completedTasks(5)
                .progressPercentage(50.0)
                .build();

        progressResponse = ProjectProgressResponse.builder()
                .projectId(1L)
                .projectTitle("Test Project")
                .totalTasks(10)
                .completedTasks(5)
                .incompleteTasks(5)
                .progressPercentage(50.0)
                .build();
    }

    @Test
    @DisplayName("Should create project successfully")
    void shouldCreateProject() {
        // Given
        when(projectService.createProject(createRequest)).thenReturn(projectResponse);

        // When
        ResponseEntity<ApiResponse<ProjectResponse>> response =
                projectController.createProject(createRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEqualTo(projectResponse);
        assertThat(response.getBody().getMessage()).isEqualTo("Project created successfully");

        verify(projectService).createProject(createRequest);
    }

    @Test
    @DisplayName("Should get all projects successfully")
    void shouldGetAllProjects() {
        // Given
        List<ProjectResponse> projects = Arrays.asList(projectResponse);
        when(projectService.getAllProjects()).thenReturn(projects);

        // When
        ResponseEntity<ApiResponse<List<ProjectResponse>>> response =
                projectController.getAllProjects();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEqualTo(projects);

        verify(projectService).getAllProjects();
    }

    @Test
    @DisplayName("Should get project by ID successfully")
    void shouldGetProjectById() {
        // Given
        when(projectService.getProjectById(1L)).thenReturn(projectResponse);

        // When
        ResponseEntity<ApiResponse<ProjectResponse>> response =
                projectController.getProjectById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEqualTo(projectResponse);

        verify(projectService).getProjectById(1L);
    }

    @Test
    @DisplayName("Should update project successfully")
    void shouldUpdateProject() {
        // Given
        when(projectService.updateProject(1L, updateRequest)).thenReturn(projectResponse);

        // When
        ResponseEntity<ApiResponse<ProjectResponse>> response =
                projectController.updateProject(1L, updateRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEqualTo(projectResponse);
        assertThat(response.getBody().getMessage()).isEqualTo("Project updated successfully");

        verify(projectService).updateProject(1L, updateRequest);
    }

    @Test
    @DisplayName("Should delete project successfully")
    void shouldDeleteProject() {
        // When
        ResponseEntity<ApiResponse<Void>> response =
                projectController.deleteProject(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo("Project deleted successfully");

        verify(projectService).deleteProject(1L);
    }

    @Test
    @DisplayName("Should get project progress successfully")
    void shouldGetProjectProgress() {
        // Given
        when(projectService.getProjectProgress(1L)).thenReturn(progressResponse);

        // When
        ResponseEntity<ApiResponse<ProjectProgressResponse>> response =
                projectController.getProjectProgress(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSuccess()).isTrue();
        assertThat(response.getBody().getData()).isEqualTo(progressResponse);

        verify(projectService).getProjectProgress(1L);
    }
}