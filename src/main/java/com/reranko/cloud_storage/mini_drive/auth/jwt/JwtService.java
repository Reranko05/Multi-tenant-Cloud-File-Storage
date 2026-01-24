package com.reranko.cloud_storage.mini_drive.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/*
 * JwtService is responsible for creating and validating JWT tokens.
 * It does not handle user authentication or authorization logic, it simply manages the tokens used for these purposes.
 * It generates tokens containing user information and checks if tokens are valid or expired.
 * 
 * It uses HMAC-SHA256 alogrithm for signing the tokens. This provides integrity and authenticity to the tokens.
 * Token consists of three parts: Header, Payload, and Signature.
 * 
 * Header: Contains metadata about the token, including the signing algorithm used.
 * Payload: Contains the data of the user such as UserId, email, token issue time, and token expiration time.
 * Signature: Ensures that the token has not been altered. It is created by signing the header and payload with a secret key. 
 *            Secret key is never sent to the client. Signature will fail if any part of the token is changed.
 * 
 * HMAC-SHA256(SECRET + HEADER + PAYLOAD) = JWT SIGNATURE
 * 
 * There are two types of JWT tokens:
 * 1. Access Token: Short-lived token used for accessing protected resources. It contains user information and permissions.
 * 2. Refresh Token: Long-lived token used to obtain new access tokens when they expire. It does not contain user information.
 * 
 * JWT will be used for every API request except for registration and login.
 * 
 * JWT generation creates a signed, time-limited identity token after login, and expiry ensures stolen tokens automatically become useless.
 * 
 * JWT token is saved on the client side, typically in local storage or cookies, and sent with each request to access protected resources.
 * It is stateless, meaning the server does not need to store session information.
 * 
 * JWT validation ensures that the token is valid, not expired, and has not been tampered with before granting access to protected resources.
 * A JWT filter runs before any controller and checks the token once for every request
 * 
 * What does JWT Filter do?
 * 1. Extracts the JWT token from the Authorization header of incoming HTTP requests.
 * 2. Validates the token using JwtService to ensure it is well-formed, not expired, and signed with the correct secret key.
 * 3. If the token is valid, it retrieves user details (like UserId) from the token and sets the authentication context for the request.
 * 4. If the token is invalid or missing, it rejects the request or allows it to proceed as an unauthenticated request based on the application's security configuration.
 * 
 * JWT Filter Validates identity and establishes trust for the request before it reaches the controller.
 * 
 */

@Service
public class JwtService {
    
    private final Key key;
    private final long expiration;

    public JwtService(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration}") long expiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    public String generateToken(Long userId, String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long extractUserId(String token) {
        return Long.parseLong(
                Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject()
        );
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
