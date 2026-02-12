package com.phillipe.NutriFit.controller;

import com.phillipe.NutriFit.dto.request.ProfileUpdateRequest;
import com.phillipe.NutriFit.dto.response.ProfileResponse;
import com.phillipe.NutriFit.dto.response.UserChangeHistoryResponse;
import com.phillipe.NutriFit.service.ChangeHistoryService;
import com.phillipe.NutriFit.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final ChangeHistoryService changeHistoryService;

    @GetMapping
    public ProfileResponse getProfile(Authentication authentication) {
        String username = authentication.getName();
        return profileService.getProfile(username);
    }

    @PutMapping
    public ProfileResponse updateProfile(@Valid @RequestBody ProfileUpdateRequest request,
                                         Authentication authentication) {
        String username = authentication.getName();
        return profileService.updateProfile(request, username);
    }

    @GetMapping("/history")
    public List<UserChangeHistoryResponse> getHistory(Authentication authentication) {
        String username = authentication.getName();
        return changeHistoryService.getHistory(username);
    }
}
