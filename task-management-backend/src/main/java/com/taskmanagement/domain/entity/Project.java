package com.taskmanagement.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Project Entity representing a project that contains multiple tasks.
 * Owned by a user and tracks progress based on task completion.
 */
@Entity
@Table(name = "projects", indexes = {
        @Index(name = "idx_project_owner", columnList = "owner_id"),
        @Index(name = "idx_project_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Many-to-one relationship with User.
     * Multiple projects belong to one user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * One-to-many relationship with Tasks.
     * When project is deleted, all its tasks are deleted (cascade)
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Task> tasks = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Helper method to add a task to project's collection.
     * Maintains bidirectional relationship consistency.
     */
    public void addTask(Task task) {
        tasks.add(task);
        task.setProject(this);
    }

    /**
     * Helper method to remove a task from project's collection.
     * Maintains bidirectional relationship consistency.
     */
    public void removeTask(Task task) {
        tasks.remove(task);
        task.setProject(null);
    }

    /**
     * Calculate project progress based on completed tasks.
     * @return Progress percentage (0-100)
     */
    public double calculateProgress() {
        if (tasks.isEmpty()) {
            return 0.0;
        }
        long completedTasks = tasks.stream()
                .filter(Task::getCompleted)
                .count();
        return (completedTasks * 100.0) / tasks.size();
    }

    /**
     * Get count of completed tasks.
     * @return Number of completed tasks
     */
    public long getCompletedTasksCount() {
        return tasks.stream()
                .filter(Task::getCompleted)
                .count();
    }

    /**
     * Get total tasks count.
     * @return Total number of tasks
     */
    public int getTotalTasksCount() {
        return tasks.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project project)) return false;
        return id != null && id.equals(project.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", tasksCount=" + tasks.size() +
                '}';
    }
}