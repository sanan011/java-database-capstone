package com.project.back_end.DTO;

public class Login {

    private String identifier; // Email, username, or other user ID
    private String password;

    // Default constructor
    public Login() {
    }

    // Constructor with parameters
    public Login(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    // Getters and Setters
    public String getEmail() {
        return identifier;
    }
    
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
