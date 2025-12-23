package com.taskmanagement.domain.repository;

import com.taskmanagement.domain.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Task entity.
 * Provides database operations for task management.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks for a specific project.
     * Returns tasks ordered by creation date (newest first).
     */
    List<Task> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    /**
     * Find task by ID and project ID.
     * Ensures tasks are accessed within their correct project context.
     */
    Optional<Task> findByIdAndProjectId(Long id, Long projectId);

    /**
     * Find all completed tasks in a project.
     */
    List<Task> findByProjectIdAndCompletedTrue(Long projectId);

    /**
     * Find all incomplete tasks in a project.
     */
    List<Task> findByProjectIdAndCompletedFalse(Long projectId);

    /**
     * Find overdue tasks in a project.
     * Tasks are overdue if they have a due date in the past and are not completed.
     */
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId " +
            "AND t.completed = false " +
            "AND t.dueDate < :currentDate")
    List<Task> findOverdueTasksByProjectId(@Param("projectId") Long projectId,
                                           @Param("currentDate") LocalDate currentDate);

    /**
     * Count total tasks in a project.
     */
    long countByProjectId(Long projectId);

    /**
     * Count completed tasks in a project.
     */
    long countByProjectIdAndCompletedTrue(Long projectId);

    /**
     * Count incomplete tasks in a project.
     */
    long countByProjectIdAndCompletedFalse(Long projectId);

    /**
     * Delete all tasks in a project.
     */
    void deleteByProjectId(Long projectId);

    /**
     * Check if task exists with given ID and project.
     */
    boolean existsByIdAndProjectId(Long id, Long projectId);
}