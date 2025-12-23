package com.taskmanagement.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Task entity.
 */
@DisplayName("Task Entity Tests")
class TaskTest {

    private Task task;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Description")
                .completed(false)
                .dueDate(LocalDate.now().plusDays(7))
                .build();
    }

    @Test
    @DisplayName("Should mark task as completed")
    void shouldMarkAsCompleted() {
        // When
        task.markAsCompleted();

        // Then
        assertThat(task.getCompleted()).isTrue();
        assertThat(task.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should mark task as incomplete")
    void shouldMarkAsIncomplete() {
        // Given
        task.markAsCompleted();

        // When
        task.markAsIncomplete();

        // Then
        assertThat(task.getCompleted()).isFalse();
        assertThat(task.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("Should not be overdue when due date is in future")
    void shouldNotBeOverdueWhenDueDateInFuture() {
        // Given
        task.setDueDate(LocalDate.now().plusDays(5));
        task.setCompleted(false);

        // When
        boolean overdue = task.isOverdue();

        // Then
        assertThat(overdue).isFalse();
    }

    @Test
    @DisplayName("Should be overdue when due date is in past")
    void shouldBeOverdueWhenDueDateInPast() {
        // Given
        task.setDueDate(LocalDate.now().minusDays(1));
        task.setCompleted(false);

        // When
        boolean overdue = task.isOverdue();

        // Then
        assertThat(overdue).isTrue();
    }

    @Test
    @DisplayName("Should not be overdue when task is completed")
    void shouldNotBeOverdueWhenCompleted() {
        // Given
        task.setDueDate(LocalDate.now().minusDays(1));
        task.setCompleted(true);

        // When
        boolean overdue = task.isOverdue();

        // Then
        assertThat(overdue).isFalse();
    }

    @Test
    @DisplayName("Should not be overdue when due date is null")
    void shouldNotBeOverdueWhenDueDateNull() {
        // Given
        task.setDueDate(null);
        task.setCompleted(false);

        // When
        boolean overdue = task.isOverdue();

        // Then
        assertThat(overdue).isFalse();
    }

    @Test
    @DisplayName("Should test equals with same ID")
    void shouldBeEqualWithSameId() {
        // Given
        Task task2 = Task.builder()
                .id(1L)
                .title("Different Title")
                .build();

        // When & Then
        assertThat(task).isEqualTo(task2);
    }

    @Test
    @DisplayName("Should have toString")
    void shouldHaveToString() {
        // When
        String toString = task.toString();

        // Then
        assertThat(toString).contains("Test Task");
        assertThat(toString).contains("false");
    }
}