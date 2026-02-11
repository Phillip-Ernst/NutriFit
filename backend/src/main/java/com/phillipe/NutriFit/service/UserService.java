package com.phillipe.NutriFit.service;

import com.phillipe.NutriFit.exception.DuplicateUsernameException;
import com.phillipe.NutriFit.repository.UserRepository;
import com.phillipe.NutriFit.model.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

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

}
