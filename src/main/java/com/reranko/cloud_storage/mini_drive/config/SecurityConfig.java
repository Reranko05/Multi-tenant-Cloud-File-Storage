package com.reranko.cloud_storage.mini_drive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Indicates that this class contains configuration settings for the application.
public class SecurityConfig{

    @Bean
    /* Bean is something you create once and give to Spring, and Spring remembers it and uses it for you automatically whenever it's needed.
     * In this case, we create a SecurityFilterChain bean that configures the security settings for our application.
     */
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            /* csrf => Cross-Site Request Forgery
             * Security protection that prevents unauthorized commands from being transmitted from a user that the web application trusts.
             * In this case, we disable it to allow all requests without CSRF protection.
             * Disabling because we are will use REST API and JWT for authentication in the future.
             */
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());
            /* httpBasic is and authentication mechnanism that sends username and password with every request using HTTP headers.
             * In this case, we disable it to allow all requests without authentication.
             */
        return http.build();
        /* build() method is called to create the SecurityFilterChain object with the configured settings.
         */
    }
}