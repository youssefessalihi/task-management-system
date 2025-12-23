package com.taskmanagement.domain.repository;

import com.taskmanagement.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides database operations for user management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address.
     * Used for authentication and uniqueness checks.
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists with given email.
     * Used for registration validation.
     */
    boolean existsByEmail(String email);

    /**
     * Find user by email with projects eagerly loaded.
     * Optimizes queries when projects are needed immediately.
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.projects WHERE u.email = :email")
    Optional<User> findByEmailWithProjects(String email);
}