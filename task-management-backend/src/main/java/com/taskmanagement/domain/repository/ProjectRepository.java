package com.taskmanagement.domain.repository;

import com.taskmanagement.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Project entity.
 * Provides database operations for project management.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Find all projects owned by a specific user.
     * Returns projects ordered by creation date (newest first).
     */
    List<Project> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);

    /**
     * Find project by ID and owner ID.
     * Ensures users can only access their own projects.
     */
    Optional<Project> findByIdAndOwnerId(Long id, Long ownerId);

    /**
     * Find project by ID with tasks eagerly loaded.
     * Optimizes queries when tasks are needed immediately.
     */
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks WHERE p.id = :id")
    Optional<Project> findByIdWithTasks(@Param("id") Long id);

    /**
     * Find project by ID and owner with tasks eagerly loaded.
     * Combines security check with performance optimization.
     */
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks WHERE p.id = :id AND p.owner.id = :ownerId")
    Optional<Project> findByIdAndOwnerIdWithTasks(@Param("id") Long id, @Param("ownerId") Long ownerId);

    /**
     * Check if project exists with given ID and owner.
     * Used for authorization checks.
     */
    boolean existsByIdAndOwnerId(Long id, Long ownerId);

    /**
     * Count total projects for a user.
     */
    long countByOwnerId(Long ownerId);
}