package com.phillipe.NutriFit.service.impl;

import com.phillipe.NutriFit.dto.request.ProfileUpdateRequest;
import com.phillipe.NutriFit.dto.response.ProfileResponse;
import com.phillipe.NutriFit.model.entity.User;
import com.phillipe.NutriFit.model.entity.UserProfile;
import com.phillipe.NutriFit.repository.UserProfileRepository;
import com.phillipe.NutriFit.repository.UserRepository;
import com.phillipe.NutriFit.service.ChangeHistoryService;
import com.phillipe.NutriFit.service.ProfileService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserProfileRepository profileRepo;
    private final UserRepository userRepo;
    private final ChangeHistoryService changeHistoryService;

    public ProfileServiceImpl(UserProfileRepository profileRepo, UserRepository userRepo,
                              ChangeHistoryService changeHistoryService) {
        this.profileRepo = profileRepo;
        this.userRepo = userRepo;
        this.changeHistoryService = changeHistoryService;
    }

    @Override
    @Transactional
    public ProfileResponse getProfile(String username) {
        User user = findUser(username);
        UserProfile profile = profileRepo.findByUserId(user.getId())
                .orElseGet(() -> createDefaultProfile(user));
        return ProfileResponse.fromEntity(profile, username);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(ProfileUpdateRequest request, String username) {
        User user = findUser(username);
        UserProfile profile = profileRepo.findByUserId(user.getId())
                .orElseGet(() -> createDefaultProfile(user));

        // Record changes before updating
        if (request.getBirthYear() != null) {
            changeHistoryService.recordChange(user, "PROFILE", null, "birthYear",
                    profile.getBirthYear(), request.getBirthYear());
            profile.setBirthYear(request.getBirthYear());
        }
        if (request.getGender() != null) {
            changeHistoryService.recordChange(user, "PROFILE", null, "gender",
                    profile.getGender(), request.getGender());
            profile.setGender(request.getGender());
        }
        if (request.getUnitPreference() != null) {
            changeHistoryService.recordChange(user, "PROFILE", null, "unitPreference",
                    profile.getUnitPreference(), request.getUnitPreference());
            profile.setUnitPreference(request.getUnitPreference());
        }

        UserProfile saved = profileRepo.save(profile);
        return ProfileResponse.fromEntity(saved, username);
    }

    private User findUser(String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }
        return user;
    }

    private UserProfile createDefaultProfile(User user) {
        UserProfile profile = UserProfile.builder()
                .user(user)
                .build();
        return profileRepo.save(profile);
    }
}
