package com.movieplatform.api.dto;

import com.movieplatform.api.model.AppUser;

public class UserResponse {
    private String userId;
    private String name;
    private String email;
    private String paid;
    private String role;

    public static UserResponse from(AppUser user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPaid(user.getPaid());
        response.setRole(user.getRole().name());
        return response;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPaid() { return paid; }
    public void setPaid(String paid) { this.paid = paid; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
