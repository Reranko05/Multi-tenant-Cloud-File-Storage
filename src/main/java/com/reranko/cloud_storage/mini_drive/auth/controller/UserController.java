package com.reranko.cloud_storage.mini_drive.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/* Why do we need this UserController?
 * This controller provides an endpoint to verify that JWT authentication is working correctly.
 * The /me endpoint returns the authenticated user's ID extracted from the JWT token.
 * This is useful for testing and debugging purposes to ensure that the JWT filter is functioning as expected.
 */

@RestController // This class listens for HTTP reuests and sends back HTTP responses
public class UserController { // Controller to handle user-related endpoints
    
    @GetMapping("/me") // This was bascially to verify that JWT auth was working
    public ResponseEntity<Long> me() {
        Long userId = (Long) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(userId);
    }
}