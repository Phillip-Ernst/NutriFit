package com.phillipe.NutriFit.service;

import com.phillipe.NutriFit.dto.request.ProfileUpdateRequest;
import com.phillipe.NutriFit.dto.response.ProfileResponse;

public interface ProfileService {
    ProfileResponse getProfile(String username);
    ProfileResponse updateProfile(ProfileUpdateRequest request, String username);
}
