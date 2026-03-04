package com.phillipe.NutriFit.service;

import com.phillipe.NutriFit.exception.DuplicateUsernameException;
import com.phillipe.NutriFit.repository.UserRepository;
import com.phillipe.NutriFit.model.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("request body is required");
        }
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("username is required");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("password is required");
        }

        String normalizedUsername = user.getUsername().trim();
        if (userRepo.existsByUsername(normalizedUsername)) {
            throw new DuplicateUsernameException(normalizedUsername);
        }

        user.setUsername(normalizedUsername);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepo.save(user);
    }

    public User findOrCreateOAuthUser(String provider, String providerId, String preferredUsername) {
        return userRepo.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    String username = generateUniqueUsername(preferredUsername.trim());
                    User user = new User();
                    user.setUsername(username);
                    user.setProvider(provider);
                    user.setProviderId(providerId);
                    return userRepo.save(user);
                });
    }

    private String generateUniqueUsername(String base) {
        // Strip characters not allowed in usernames (keep alphanumeric + underscore + hyphen)
        String sanitized = base.replaceAll("[^a-zA-Z0-9_\\-]", "");
        if (sanitized.isBlank()) {
            sanitized = "user";
        }
        if (!userRepo.existsByUsername(sanitized)) {
            return sanitized;
        }
        // Append a short random suffix until unique
        String candidate;
        do {
            candidate = sanitized + "_" + UUID.randomUUID().toString().substring(0, 6);
        } while (userRepo.existsByUsername(candidate));
        return candidate;
    }

}
