package com.payhub.dto;

public class SendCodeRequestDto {
    private String email;
    private String type; // 例如: "REGISTRATION", "PASSWORD_RESET"

    // --- Getters ---
    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    // --- Setters ---
    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SendCodeRequestDto{" +
                "email='" + email + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}