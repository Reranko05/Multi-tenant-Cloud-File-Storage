package com.reranko.cloud_storage.mini_drive.auth.service;

import com.reranko.cloud_storage.mini_drive.auth.dto.RegisterRequest;
import com.reranko.cloud_storage.mini_drive.user.entity.User;
import com.reranko.cloud_storage.mini_drive.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/* 
 * AuthService contains the core business logic for user authentication, such as registering new users.
 * It interacts with the UserRepository to perform database operations related to users.
 * It takes the user data from RegisterRequest and processes it to create a new User in the system.
 * 
 * It takes login data from LoginRequest and verifies user credentials.
 */

@Service // Tells Spring that this class contains business logic related to authentication
public class AuthService {

    private final UserRepository userRepository; // Repository to interact with User data in the database
    private final PasswordEncoder passwordEncoder; // Service to handle password hashing and verification

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest request) { // Method to handle new user registration
        if (userRepository.existsByEmail(request.getEmail())) { // Check if the email is already in use in database
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user); // Save the new user to the database
    }

    public void Login(LoginRequest request) { // Method to handle existing user login
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
        boolean passwordMatches = passwordEncoder.matches( // Check if the provided password matches the stored hashed password
                request.getPassword(),
                user.getPassword()
        );

        if(!passwordMatches) {
            throw new RuntimeException("Invalid password");
        }

        // login successful (JWT will be added later)
    }
}