package com.movieplatform.api.controller;

import com.movieplatform.api.dto.ProfileUpdateRequest;
import com.movieplatform.api.dto.UserResponse;
import com.movieplatform.api.model.AppUser;
import com.movieplatform.api.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserResponse getProfile(@PathVariable String userId) {
        return UserResponse.from(userService.getUser(userId));
    }

    @PutMapping("/{userId}")
    public UserResponse updateProfile(@PathVariable String userId, @Valid @RequestBody ProfileUpdateRequest request) {
        return UserResponse.from(userService.updateProfile(userId, request));
    }

    @GetMapping
    public List<UserResponse> allUsers() {
        return userService.allUsers().stream().map(UserResponse::from).toList();
    }
}
