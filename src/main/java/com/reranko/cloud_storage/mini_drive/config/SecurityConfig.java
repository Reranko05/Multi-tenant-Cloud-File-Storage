package com.reranko.cloud_storage.mini_drive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.reranko.cloud_storage.mini_drive.auth.jwt.JwtAuthenticationFilter;
import com.reranko.cloud_storage.mini_drive.auth.jwt.JwtService;



@Configuration // Indicates that this class contains configuration settings for the application.
public class SecurityConfig{

    @Bean
    /* Bean is something you create once and give to Spring, and Spring remembers it and uses it for you automatically whenever it's needed.
     * In this case, we create a SecurityFilterChain bean that configures the security settings for our application.
     */
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            /* csrf => Cross-Site Request Forgery
             * Security protection that prevents unauthorized commands from being transmitted from a user that the web application trusts.
             * In this case, we disable it to allow all requests without CSRF protection.
             * Disabling because we are will use REST API and JWT for authentication in the future.
             */
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/auth/**",
                    "/h2-console/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                    new JwtAuthenticationFilter(jwtService),
                    UsernamePasswordAuthenticationFilter.class
            )
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .sessionManagement(session -> session
                    .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
            );

        

        return http.build();
        /* build() method is called to create the SecurityFilterChain object with the configured settings.
         */
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(java.util.Arrays.asList("http://localhost:5500", "http://127.0.0.1:5500"));
        configuration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}