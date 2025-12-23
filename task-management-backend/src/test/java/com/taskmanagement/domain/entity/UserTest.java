package com.taskmanagement.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for User entity.
 */
@DisplayName("User Entity Tests")
class UserTest {

    private User user;
    private Project project;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("password")
                .role(User.Role.USER)
                .enabled(true)
                .build();

        project = Project.builder()
                .id(1L)
                .title("Test Project")
                .description("Description")
                .build();
    }

    @Test
    @DisplayName("Should add project to user")
    void shouldAddProject() {
        // When
        user.addProject(project);

        // Then
        assertThat(user.getProjects()).contains(project);
        assertThat(project.getOwner()).isEqualTo(user);
    }

    @Test
    @DisplayName("Should remove project from user")
    void shouldRemoveProject() {
        // Given
        user.addProject(project);

        // When
        user.removeProject(project);

        // Then
        assertThat(user.getProjects()).doesNotContain(project);
        assertThat(project.getOwner()).isNull();
    }

    @Test
    @DisplayName("Should test equals with same ID")
    void shouldBeEqualWithSameId() {
        // Given
        User user2 = User.builder()
                .id(1L)
                .email("different@example.com")
                .build();

        // When & Then
        assertThat(user).isEqualTo(user2);
    }

    @Test
    @DisplayName("Should not be equal with different ID")
    void shouldNotBeEqualWithDifferentId() {
        // Given
        User user2 = User.builder()
                .id(2L)
                .email("test@example.com")
                .build();

        // When & Then
        assertThat(user).isNotEqualTo(user2);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        // When & Then
        assertThat(user).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should not be equal to different class")
    void shouldNotBeEqualToDifferentClass() {
        // When & Then
        assertThat(user).isNotEqualTo("String");
    }

    @Test
    @DisplayName("Should have consistent hashCode")
    void shouldHaveConsistentHashCode() {
        // Given
        int hashCode1 = user.hashCode();
        int hashCode2 = user.hashCode();

        // When & Then
        assertThat(hashCode1).isEqualTo(hashCode2);
    }

    @Test
    @DisplayName("Should have toString")
    void shouldHaveToString() {
        // When
        String toString = user.toString();

        // Then
        assertThat(toString).contains("Test User");
        assertThat(toString).contains("test@example.com");
        assertThat(toString).contains("USER");
    }
}