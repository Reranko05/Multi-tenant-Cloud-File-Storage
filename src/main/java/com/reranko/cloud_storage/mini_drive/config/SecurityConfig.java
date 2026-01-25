package com.reranko.cloud_storage.mini_drive.config;

import com.reranko.cloud_storage.mini_drive.auth.jwt.JwtAuthenticationFilter;
import com.reranko.cloud_storage.mini_drive.auth.jwt.JwtService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;



@Configuration // Indicates that this class contains configuration settings for the application.
public class SecurityConfig{

    @Bean
    /* Bean is something you create once and give to Spring, and Spring remembers it and uses it for you automatically whenever it's needed.
     * In this case, we create a SecurityFilterChain bean that configures the security settings for our application.
     */
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            /* csrf => Cross-Site Request Forgery
             * Security protection that prevents unauthorized commands from being transmitted from a user that the web application trusts.
             * In this case, we disable it to allow all requests without CSRF protection.
             * Disabling because we are will use REST API and JWT for authentication in the future.
             */
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    HttpMethod.POST,
                    "/auth/register",
                    "/auth/login"
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
}