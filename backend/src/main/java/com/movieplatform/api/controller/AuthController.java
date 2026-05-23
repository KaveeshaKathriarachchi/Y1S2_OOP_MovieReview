package com.movieplatform.api.controller;

import com.movieplatform.api.dto.AuthResponse;
import com.movieplatform.api.dto.LoginRequest;
import com.movieplatform.api.dto.RegisterRequest;
import com.movieplatform.api.dto.UserResponse;
import com.movieplatform.api.model.AppUser;
import com.movieplatform.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        AppUser user = userService.register(request);
        return new AuthResponse("Registration saved. User can now log in.", UserResponse.from(user));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        AppUser user = userService.login(request);
        String target = user.getRole().name().equals("ADMIN") ? "ADMIN_DASHBOARD" : "MOVIE_PAGE";
        return new AuthResponse(target, UserResponse.from(user));
    }
}
