package com.taskmanagement.application.dto.response;

import com.taskmanagement.domain.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for project information in responses.
 * Includes progress statistics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

    private Long id;
    private String title;
    private String description;
    private Long ownerId;
    private String ownerName;
    private Integer totalTasks;
    private Integer completedTasks;
    private Double progressPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert Project entity to ProjectResponse DTO.
     */
    public static ProjectResponse fromEntity(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .ownerId(project.getOwner() != null ? project.getOwner().getId() : null)
                .ownerName(project.getOwner() != null ? project.getOwner().getName() : "Unknown")
                .totalTasks(project.getTotalTasksCount())
                .completedTasks((int) project.getCompletedTasksCount())
                .progressPercentage(Math.round(project.calculateProgress() * 100.0) / 100.0)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    /**
     * Create ProjectResponse without loading tasks (optimized).
     */
    public static ProjectResponse fromEntity(Project project, int totalTasks, int completedTasks) {
        double progress = totalTasks > 0 ? (completedTasks * 100.0) / totalTasks : 0.0;

        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .ownerId(project.getOwner() != null ? project.getOwner().getId() : null)
                .ownerName(project.getOwner() != null ? project.getOwner().getName() : "Unknown")
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .progressPercentage(Math.round(progress * 100.0) / 100.0)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}