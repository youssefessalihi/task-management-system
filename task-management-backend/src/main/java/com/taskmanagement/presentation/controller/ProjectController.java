package com.taskmanagement.presentation.controller;

import com.taskmanagement.application.dto.request.CreateProjectRequest;
import com.taskmanagement.application.dto.request.UpdateProjectRequest;
import com.taskmanagement.application.dto.response.ApiResponse;
import com.taskmanagement.application.dto.response.ProjectProgressResponse;
import com.taskmanagement.application.dto.response.ProjectResponse;
import com.taskmanagement.application.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for project management.
 * Handles CRUD operations for projects.
 */
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Projects", description = "Project management endpoints")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Create a new project.
     */
    @PostMapping
    @Operation(summary = "Create project", description = "Create a new project")
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse response = projectService.createProject(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created successfully", response));
    }

    /**
     * Get all projects for current user.
     */
    @GetMapping
    @Operation(summary = "Get all projects", description = "Get all projects for the authenticated user")
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAllProjects() {
        List<ProjectResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(ApiResponse.success(projects));
    }

    /**
     * Get project by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID", description = "Get project details by ID")
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(@PathVariable Long id) {
        ProjectResponse response = projectService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update project.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update project", description = "Update project details")
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest request) {
        ProjectResponse response = projectService.updateProject(id, request);
        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", response));
    }

    /**
     * Delete project.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete project", description = "Delete a project and all its tasks")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully"));
    }

    /**
     * Get project progress.
     */
    @GetMapping("/{id}/progress")
    @Operation(summary = "Get project progress", description = "Get task completion progress for a project")
    public ResponseEntity<ApiResponse<ProjectProgressResponse>> getProjectProgress(@PathVariable Long id) {
        ProjectProgressResponse response = projectService.getProjectProgress(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}