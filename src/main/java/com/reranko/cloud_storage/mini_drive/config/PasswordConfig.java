package com.reranko.cloud_storage.mini_drive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/*
 * PasswordConfig is responsible for configuring password into hashed format using BCrypt hashing algorithm.
 * BCrypt is one-way hashing function that converts plain text passwords into a hashed format.
 * It also adds a unique salt to passwords before hashing which is unique to each user, so even if two users have same password, their hashed passwords will be different.
 * It is stored like [BCrypt Version]$[Cost Factor]$[Salt][Hash(Password + Salt)] during User Registration.
 * During Login, the system looks for the account using the email provided, retrieves the stored hashed password, extracts the salt from it, combines it with the
 * entered password, hashes the combination using the same cost factor, and compares it with the stored password to verify if they match.
 * hashed(inputPassword + extractedSalt) == hash(password + salt) => passwords match
 * 
 * BCrypt Version :- 
 * Identifies the version of the BCrypt algorithm used.
 * $2a$ classic version 
 * $2b$ current version
 * $2y$ specific to PHP's implementation
 * 
 * Cost Factor :-
 * Determines the computational complexity of the hashing process, bcrypt runs hashing 2^cost times.
 * Higher cost factor increases security but requires more processing time.
 * Cost 10 → ~1 ms–10 ms ; Cost 12 → ~10 ms–100 ms ~4× slower ; Cost 14 → ~100 ms–1 s ~16× slower
 * A common default cost factor is 10.
 * 
 * new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2B,12); -> Example of creating a BCryptPasswordEncoder with version 2B and cost factor 12
 */

@Configuration 
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncode() {
        return new BCryptPasswordEncoder();
    }
}