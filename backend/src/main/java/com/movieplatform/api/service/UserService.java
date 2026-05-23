package com.movieplatform.api.service;

import java.util.List;
import java.util.OptionalInt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser register(RegisterRequest request) {
        String userId = request.getUserId();
        if (userId == null || userId.isBlank()) {
            userId = generateNextUserId();
        }

        if (userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User ID already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        AppUser user = new AppUser();
        user.setUserId(userId);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPaid("no");
        user.setRole("ADMIN".equalsIgnoreCase(request.getRole()) ? UserRole.ADMIN : UserRole.USER);
        return userRepository.save(user);
    }

    public AppUser login(LoginRequest request) {
        AppUser user = getUser(request.getUserId());
        boolean hashMatches = user.getPasswordHash() != null
                && !user.getPasswordHash().isBlank()
                && passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
        boolean plainPasswordMatches = user.getPassword() != null
                && user.getPassword().equals(request.getPassword());

        if (!hashMatches && !plainPasswordMatches) {
            throw new IllegalArgumentException("Invalid user ID or password");
        }

        if (plainPasswordMatches && (user.getPasswordHash() == null || user.getPasswordHash().isBlank())) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);
        }

        return user;
    }

    public AppUser getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public AppUser updateProfile(String userId, ProfileUpdateRequest request) {
        AppUser user = getUser(userId);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(request.getPassword());
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        return userRepository.save(user);
    }

    public AppUser updatePaidStatus(String userId, boolean paid) {
        AppUser user = getUser(userId);
        user.setPaid(paid ? "yes" : "no");
        return userRepository.save(user);
    }

    public void requireAdmin(String adminUserId) {
        AppUser admin = getUser(adminUserId);
        if (admin.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Admin access required");
        }
    }

    public List<AppUser> allUsers() {
        return userRepository.findAll();
    }

    private String generateNextUserId() {
        OptionalInt maxId = userRepository.findAll().stream()
                .map(AppUser::getUserId)
                .filter(id -> id != null && id.matches("U\\d+"))
                .mapToInt(id -> Integer.parseInt(id.substring(1)))
                .max();

        int next = maxId.orElse(0) + 1;
        return "U" + String.format("%03d", next);
    }
}
