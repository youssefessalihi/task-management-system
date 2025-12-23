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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for task management operations.
 * Handles CRUD operations for tasks within projects.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    /**
     * Get current authenticated user.
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with email: " + email)
                );
    }

    /**
     * Verify user owns the project.
     */
    private void verifyProjectOwnership(Long projectId, Long userId) {
        if (!projectRepository.existsByIdAndOwnerId(projectId, userId)) {
            throw new UnauthorizedException("You don't have access to this project");
        }
    }

    /**
     * Create a new task in a project.
     */
    @Transactional
    public TaskResponse createTask(Long projectId, CreateTaskRequest request) {
        User currentUser = getCurrentUser();
        verifyProjectOwnership(projectId, currentUser.getId());

        String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "unknown";
        log.info("Creating task in project {} for user: {}", projectId, userEmail);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .project(project)
                .completed(false)
                .build();

        task = taskRepository.save(task);
        log.info("Task created with ID: {}", task.getId());

        return TaskResponse.fromEntity(task);
    }

    /**
     * Get all tasks in a project.
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasksInProject(Long projectId) {
        User currentUser = getCurrentUser();
        verifyProjectOwnership(projectId, currentUser.getId());

        String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "unknown";
        log.info("Fetching all tasks for project {} for user: {}", projectId, userEmail);

        List<Task> tasks = taskRepository.findByProjectIdOrderByCreatedAtDesc(projectId);

        return tasks.stream()
                .map(TaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get task by ID.
     */
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long projectId, Long taskId) {
        User currentUser = getCurrentUser();
        verifyProjectOwnership(projectId, currentUser.getId());

        String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "unknown";
        log.info("Fetching task {} in project {} for user: {}", taskId, projectId, userEmail);

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        return TaskResponse.fromEntity(task);
    }

    /**
     * Update task.
     */
    @Transactional
    public TaskResponse updateTask(Long projectId, Long taskId, UpdateTaskRequest request) {
        User currentUser = getCurrentUser();
        verifyProjectOwnership(projectId, currentUser.getId());

        String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "unknown";
        log.info("Updating task {} in project {} for user: {}", taskId, projectId, userEmail);

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getCompleted() != null) {
            if (request.getCompleted()) {
                task.markAsCompleted();
            } else {
                task.markAsIncomplete();
            }
        }

        task = taskRepository.save(task);
        log.info("Task {} updated successfully", taskId);

        return TaskResponse.fromEntity(task);
    }

    /**
     * Mark task as completed.
     */
    @Transactional
    public TaskResponse markTaskAsCompleted(Long projectId, Long taskId) {
        User currentUser = getCurrentUser();
        verifyProjectOwnership(projectId, currentUser.getId());

        String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "unknown";
        log.info("Marking task {} as completed in project {} for user: {}",
                taskId, projectId, userEmail);

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        task.markAsCompleted();
        task = taskRepository.save(task);

        log.info("Task {} marked as completed", taskId);
        return TaskResponse.fromEntity(task);
    }

    /**
     * Delete task.
     */
    @Transactional
    public void deleteTask(Long projectId, Long taskId) {
        User currentUser = getCurrentUser();
        verifyProjectOwnership(projectId, currentUser.getId());

        String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "unknown";
        log.info("Deleting task {} in project {} for user: {}", taskId, projectId, userEmail);

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        taskRepository.delete(task);
        log.info("Task {} deleted successfully", taskId);
    }
}