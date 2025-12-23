package com.taskmanagement.presentation.controller;

import com.taskmanagement.application.dto.request.CreateTaskRequest;
import com.taskmanagement.application.dto.request.UpdateTaskRequest;
import com.taskmanagement.application.dto.response.ApiResponse;
import com.taskmanagement.application.dto.response.TaskResponse;
import com.taskmanagement.application.service.TaskService;
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
 * REST Controller for task management.
 * Handles CRUD operations for tasks within projects.
 */
@RestController
@RequestMapping("/api/v1/projects/{projectId}/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tasks", description = "Task management endpoints")
public class TaskController {

    private final TaskService taskService;

    /**
     * Create a new task in a project.
     */
    @PostMapping
    @Operation(summary = "Create task", description = "Create a new task in a project")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequest request) {
        TaskResponse response = taskService.createTask(projectId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created successfully", response));
    }

    /**
     * Get all tasks in a project.
     */
    @GetMapping
    @Operation(summary = "Get all tasks", description = "Get all tasks in a project")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAllTasks(@PathVariable Long projectId) {
        List<TaskResponse> tasks = taskService.getAllTasksInProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    /**
     * Get task by ID.
     */
    @GetMapping("/{taskId}")
    @Operation(summary = "Get task by ID", description = "Get task details by ID")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(
            @PathVariable Long projectId,
            @PathVariable Long taskId) {
        TaskResponse response = taskService.getTaskById(projectId, taskId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update task.
     */
    @PutMapping("/{taskId}")
    @Operation(summary = "Update task", description = "Update task details")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskResponse response = taskService.updateTask(projectId, taskId, request);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", response));
    }

    /**
     * Mark task as completed.
     */
    @PatchMapping("/{taskId}/complete")
    @Operation(summary = "Mark task as completed", description = "Mark a task as completed")
    public ResponseEntity<ApiResponse<TaskResponse>> markTaskAsCompleted(
            @PathVariable Long projectId,
            @PathVariable Long taskId) {
        TaskResponse response = taskService.markTaskAsCompleted(projectId, taskId);
        return ResponseEntity.ok(ApiResponse.success("Task marked as completed", response));
    }

    /**
     * Delete task.
     */
    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete task", description = "Delete a task")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId) {
        taskService.deleteTask(projectId, taskId);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully"));
    }
}