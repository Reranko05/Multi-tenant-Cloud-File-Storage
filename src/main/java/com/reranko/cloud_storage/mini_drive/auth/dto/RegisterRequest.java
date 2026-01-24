package com.reranko.cloud_storage.mini_drive.auth.dto;

public class RegisterRequest {

    private String email;
    private String password;

    /* Getter read data
     * Setter write data
     * Constructors create objects
     */

    public RegisterRequest() {  // Default constructor used to crete an empty object
    }

    public String getEmail() {  // Returns the email value by the user
        return email; 
    }

    public void setEmail(String email) { // Sets the email value from the incoming request data
        this.email = email;
    }

    public String getPassword() {  // Returns the password value by the user
        return password;
    }

    public void setPassword(String password) { // Sets the password value from the incoming request data
        this.password = password;
    }
}