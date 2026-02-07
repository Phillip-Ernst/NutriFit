package com.phillipe.NutriFit.controller;

import com.phillipe.NutriFit.dto.request.LoginRequest;
import com.phillipe.NutriFit.dto.request.RegisterRequest;
import com.phillipe.NutriFit.dto.response.UserResponse;
import com.phillipe.NutriFit.model.entity.User;
import com.phillipe.NutriFit.service.JwtService;
import com.phillipe.NutriFit.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        return UserResponse.fromEntity(userService.saveUser(user));
    }

    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest request) {


        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(authentication.getName());
        } else {
            throw new BadCredentialsException("Invalid username or password.");
        }
    }
}
