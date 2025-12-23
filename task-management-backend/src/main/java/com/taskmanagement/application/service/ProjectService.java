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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for project management operations.
 * Handles CRUD operations for projects.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    /**
     * Get current authenticated user.
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("Unauthenticated user");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Create a new project for the current user.
     */
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        User currentUser = getCurrentUser();
        String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "unknown";
        log.info("Creating project for user: {}", userEmail);

        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .owner(currentUser)
                .build();

        project = projectRepository.save(project);
        log.info("Project created with ID: {}", project.getId());

        return ProjectResponse.fromEntity(project);
    }

    /**
     * Get all projects for current user.
     */
    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {
        User currentUser = getCurrentUser();
        String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "unknown";
        log.info("Fetching all projects for user: {}", userEmail);

        List<Project> projects = projectRepository.findByOwnerIdOrderByCreatedAtDesc(currentUser.getId());

        return projects.stream()
                .map(project -> {
                    int totalTasks = (int) taskRepository.countByProjectId(project.getId());
                    int completedTasks = (int) taskRepository.countByProjectIdAndCompletedTrue(project.getId());
                    return ProjectResponse.fromEntity(project, totalTasks, completedTasks);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get project by ID.
     * Ensures user owns the project.
     */
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {
        User currentUser = getCurrentUser();
        String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "unknown";
        log.info("Fetching project {} for user: {}", id, userEmail);

        Project project = projectRepository.findByIdAndOwnerId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        int totalTasks = (int) taskRepository.countByProjectId(project.getId());
        int completedTasks = (int) taskRepository.countByProjectIdAndCompletedTrue(project.getId());

        return ProjectResponse.fromEntity(project, totalTasks, completedTasks);
    }

    /**
     * Update project.
     * Ensures user owns the project.
     */
    @Transactional
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {
        User currentUser = getCurrentUser();
        String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "unknown";
        log.info("Updating project {} for user: {}", id, userEmail);

        Project project = projectRepository.findByIdAndOwnerId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        if (request.getTitle() != null) {
            project.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }

        project = projectRepository.save(project);
        log.info("Project {} updated successfully", id);

        int totalTasks = (int) taskRepository.countByProjectId(project.getId());
        int completedTasks = (int) taskRepository.countByProjectIdAndCompletedTrue(project.getId());

        return ProjectResponse.fromEntity(project, totalTasks, completedTasks);
    }

    /**
     * Delete project.
     * Ensures user owns the project.
     */
    @Transactional
    public void deleteProject(Long id) {
        User currentUser = getCurrentUser();
        String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "unknown";
        log.info("Deleting project {} for user: {}", id, userEmail);

        Project project = projectRepository.findByIdAndOwnerId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        projectRepository.delete(project);
        log.info("Project {} deleted successfully", id);
    }

    /**
     * Get project progress statistics.
     */
    @Transactional(readOnly = true)
    public ProjectProgressResponse getProjectProgress(Long id) {
        User currentUser = getCurrentUser();
        String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "unknown";
        log.info("Fetching progress for project {} for user: {}", id, userEmail);

        Project project = projectRepository.findByIdAndOwnerId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));

        int totalTasks = (int) taskRepository.countByProjectId(project.getId());
        int completedTasks = (int) taskRepository.countByProjectIdAndCompletedTrue(project.getId());

        return ProjectProgressResponse.of(project.getId(), project.getTitle(), totalTasks, completedTasks);
    }
}