package com.reranko.cloud_storage.mini_drive.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.Collections;
import java.util.List;

/* 
 * JwtAuthenticationFilter is a filter that intercepts incoming HTTP requests to check for a valid JWT token in the Authorization header.
 * If a valid token is found, it extracts the user ID from the token and sets the authentication in the SecurityContext.
 * This allows the application to identify the user making the request based on the provided JWT token.
 * 
 * The JWT Filter is the single place where trust is established for every request.
 * If the request is rejected in the filter, it will never reach the controller, business logic never runs, and data is not touched.
 * This makes Filter the security gatekeeper for the entire application.
 * 
 * Steps of JWT Authentication Filter:
 * 1. The filter is triggered automatically for every request because Spring Security is configured to use it.
 * 2. It checks the Authorization header for a Bearer token.
 *    - Case A : Header is missing -> this means that user is anonymous user, so Filter does not authenticate. Request proceeds as unauthenticated.
 *    - Case B : Header is present -> Filter extracts the JWT and moves to validation.
 * 3. It calls JwtService to validate the token. It checks if the token is well-formed, has a valid signature, has a token expiration time, and if it was signed using our secret key.
 *   - Case A : Token is invalid -> Filter does not authenticate. Request proceeds as unauthenticated.
 *   - Case B : Token is valid -> Filter extracts the user ID from the token.
 * 4. Attach the user ID to the SecurityContext as an authenticated principal.
 * 5. The request proceeds to the controller with the authenticated user context.
 * 
 * What controllers see after JWT Filter runs:
 * - If the request had a valid JWT token, controllers can access the authenticated user ID from the SecurityContext.
 * - If the request did not have a valid token, controllers see the request as unauthenticated.
 * 
 * What happens for invalid or missing tokens:
 * - Missing Token: Request is anonymous. Spring Security decides what to do based on security configuration (e.g., allow or deny access).
 * - Invalid Token: Request is treated as unauthenticated. Spring returns 401 Unautorized. Controller never called.
 * 
 * The JWT filter is a gatekeeper that validates identity once per request and tells Spring who the user is before any business logic runs.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
        ) throws java.io.IOException, jakarta.servlet.ServletException {

            String header = request.getHeader("Authorization");

            if(header == null || !header.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = header.substring(7);
            System.out.println("JWT FILTER HIT: " + request.getRequestURI());

            if(jwtService.isTokenValid(token)) {
                Long userId = jwtService.extractUserId(token);

                System.out.println("JWT VALID, setting authentication for userId=" + userId);


                UsernamePasswordAuthenticationToken auth = 
                    new UsernamePasswordAuthenticationToken(
                        userId, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

            filterChain.doFilter(request, response);
        }

}

