package com.reranko.cloud_storage.mini_drive.config;

import org.reranko.cloud_storage.mini_drive.auth.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/* AuthController just a middleman that receives HTTP requests from RegisterRequest and sends back a response.
 * It does not any verification or processing logic for the data, it simply acknowledges the receipt of the registration request.
 */

@RestController // This class listens for HTTP reuests and sends back HTTP responses
@RequestMapping("/auth") // All URLs in this controller will start with /auth
public class AuthController {

    @PostMapping("/register") // When someone sends a POST request to /auth/register, this method is called
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) { // Take the JSON sent by the user and put it into a Java object
        return ResponseEntity.ok("Registration request received"); // Send back a simple response saying we got the request
    }

    @PostMapping("/login") // When someone sends a POST request to /auth/login, this method is called
    public ResponseEntity<String> login(@RequestBody LoginRequest request) { // Take the JSON sent by the user and put it into a Java object    
        authService.login(request); // Call the AuthService to handle the login logic
        return ResponseEntity.ok("Login successful"); // Send back a simple response saying login was successful

    }
}