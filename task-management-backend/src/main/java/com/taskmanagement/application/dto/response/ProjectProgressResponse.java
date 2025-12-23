package com.taskmanagement.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for project progress information.
 * Provides detailed statistics about task completion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectProgressResponse {

    private Long projectId;
    private String projectTitle;
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer incompleteTasks;
    private Double progressPercentage;

    /**
     * Create progress response from counts.
     */
    public static ProjectProgressResponse of(Long projectId, String projectTitle,
                                             int totalTasks, int completedTasks) {
        double progress = totalTasks > 0 ? (completedTasks * 100.0) / totalTasks : 0.0;

        return ProjectProgressResponse.builder()
                .projectId(projectId)
                .projectTitle(projectTitle)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .incompleteTasks(totalTasks - completedTasks)
                .progressPercentage(Math.round(progress * 100.0) / 100.0)
                .build();
    }
}