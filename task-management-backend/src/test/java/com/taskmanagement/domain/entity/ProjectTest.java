package com.taskmanagement.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Project entity.
 */
@DisplayName("Project Entity Tests")
class ProjectTest {

    private Project project;
    private Task task;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .email("test@example.com")
                .build();

        project = Project.builder()
                .id(1L)
                .title("Test Project")
                .description("Description")
                .owner(owner)
                .build();

        task = Task.builder()
                .id(1L)
                .title("Test Task")
                .completed(false)
                .build();
    }

    @Test
    @DisplayName("Should add task to project")
    void shouldAddTask() {
        // When
        project.addTask(task);

        // Then
        assertThat(project.getTasks()).contains(task);
        assertThat(task.getProject()).isEqualTo(project);
    }

    @Test
    @DisplayName("Should remove task from project")
    void shouldRemoveTask() {
        // Given
        project.addTask(task);

        // When
        project.removeTask(task);

        // Then
        assertThat(project.getTasks()).doesNotContain(task);
        assertThat(task.getProject()).isNull();
    }

    @Test
    @DisplayName("Should calculate progress with no tasks")
    void shouldCalculateProgressWithNoTasks() {
        // When
        double progress = project.calculateProgress();

        // Then
        assertThat(progress).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should calculate progress with some completed tasks")
    void shouldCalculateProgressWithSomeCompletedTasks() {
        // Given
        Task task1 = Task.builder().id(1L).completed(true).build();
        Task task2 = Task.builder().id(2L).completed(false).build();
        Task task3 = Task.builder().id(3L).completed(true).build();

        project.addTask(task1);
        project.addTask(task2);
        project.addTask(task3);

        // When
        double progress = project.calculateProgress();

        // Then
        assertThat(progress).isBetween(66.66, 66.67); // 2/3 * 100 ≈ 66.67%
    }

    @Test
    @DisplayName("Should calculate progress with all completed tasks")
    void shouldCalculateProgressWithAllCompletedTasks() {
        // Given
        Task task1 = Task.builder().id(1L).completed(true).build();
        Task task2 = Task.builder().id(2L).completed(true).build();

        project.addTask(task1);
        project.addTask(task2);

        // When
        double progress = project.calculateProgress();

        // Then
        assertThat(progress).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Should get completed tasks count")
    void shouldGetCompletedTasksCount() {
        // Given
        Task task1 = Task.builder().id(1L).completed(true).build();
        Task task2 = Task.builder().id(2L).completed(false).build();
        Task task3 = Task.builder().id(3L).completed(true).build();

        project.addTask(task1);
        project.addTask(task2);
        project.addTask(task3);

        // When
        long count = project.getCompletedTasksCount();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should get total tasks count")
    void shouldGetTotalTasksCount() {
        // Given
        project.addTask(Task.builder().id(1L).build());
        project.addTask(Task.builder().id(2L).build());
        project.addTask(Task.builder().id(3L).build());

        // When
        int count = project.getTotalTasksCount();

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("Should test equals with same ID")
    void shouldBeEqualWithSameId() {
        // Given
        Project project2 = Project.builder()
                .id(1L)
                .title("Different Title")
                .build();

        // When & Then
        assertThat(project).isEqualTo(project2);
    }

    @Test
    @DisplayName("Should have toString")
    void shouldHaveToString() {
        // When
        String toString = project.toString();

        // Then
        assertThat(toString).contains("Test Project");
        assertThat(toString).contains("1");
    }
}