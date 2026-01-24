package com.reranko.cloud_storage.mini_drive.user.repository;

import com.reranko.cloud_storage.mini_drive.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/* 
 * UserRepository only defines what database operations are available for User entities.
 * It does not decide when or why these methods are called, it simply defines how to perform operations like finding a user by email or checking if a user exists.
 */

public interface UserRepository extends JpaRepository<User, Long> { // This means that this repository manages Users, and Primary Key type is Long

    /*
     * Login logic
     * Method to find a user whose email matches this value and Optional is used to handle the case where no user is found
     */
    Optional<User> findByEmail(String email);

    /*
     * Registration logic
     * Method to check if a user with this email already exists, returning true if found, false otherwise
     */
    boolean existsByEmail(String email);
}