package com.phillipe.NutriFit.service;

import com.phillipe.NutriFit.dto.request.ProfileUpdateRequest;
import com.phillipe.NutriFit.dto.response.ProfileResponse;
import com.phillipe.NutriFit.model.Gender;
import com.phillipe.NutriFit.model.UnitPreference;
import com.phillipe.NutriFit.model.entity.User;
import com.phillipe.NutriFit.model.entity.UserProfile;
import com.phillipe.NutriFit.repository.UserProfileRepository;
import com.phillipe.NutriFit.repository.UserRepository;
import com.phillipe.NutriFit.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private UserProfileRepository profileRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private ChangeHistoryService changeHistoryService;

    @InjectMocks
    private ProfileServiceImpl service;

    @Test
    void getProfile_shouldReturnExistingProfile() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        UserProfile profile = UserProfile.builder()
                .id(1L)
                .user(user)
                .birthYear(1990)
                .gender(Gender.MALE)
                .unitPreference(UnitPreference.IMPERIAL)
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(profileRepo.findByUserId(1L)).thenReturn(Optional.of(profile));

        // act
        ProfileResponse response = service.getProfile("testuser");

        // assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals(1990, response.getBirthYear());
        assertEquals(Gender.MALE, response.getGender());
        assertEquals(UnitPreference.IMPERIAL, response.getUnitPreference());

        verify(profileRepo, never()).save(any());
    }

    @Test
    void getProfile_shouldCreateDefaultProfileWhenNotExists() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(profileRepo.findByUserId(1L)).thenReturn(Optional.empty());
        when(profileRepo.save(any(UserProfile.class))).thenAnswer(invocation -> {
            UserProfile saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // act
        ProfileResponse response = service.getProfile("testuser");

        // assert
        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals(UnitPreference.IMPERIAL, response.getUnitPreference());
        assertNull(response.getBirthYear());
        assertNull(response.getGender());

        verify(profileRepo).save(any(UserProfile.class));
    }

    @Test
    void getProfile_shouldCalculateAgeFromBirthYear() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        int currentYear = java.time.Year.now().getValue();
        int birthYear = currentYear - 30;

        UserProfile profile = UserProfile.builder()
                .id(1L)
                .user(user)
                .birthYear(birthYear)
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(profileRepo.findByUserId(1L)).thenReturn(Optional.of(profile));

        // act
        ProfileResponse response = service.getProfile("testuser");

        // assert
        assertEquals(30, response.getAge());
    }

    @Test
    void getProfile_shouldThrowWhenUserNotFound() {
        // arrange
        when(userRepo.findByUsername("unknownuser")).thenReturn(null);

        // act & assert
        assertThrows(UsernameNotFoundException.class,
                () -> service.getProfile("unknownuser"));

        verify(userRepo).findByUsername("unknownuser");
        verifyNoInteractions(profileRepo);
    }

    @Test
    void updateProfile_shouldUpdateBirthYear() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        UserProfile profile = UserProfile.builder()
                .id(1L)
                .user(user)
                .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .birthYear(1995)
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(profileRepo.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(profileRepo.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        ProfileResponse response = service.updateProfile(request, "testuser");

        // assert
        assertEquals(1995, response.getBirthYear());
    }

    @Test
    void updateProfile_shouldUpdateGender() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        UserProfile profile = UserProfile.builder()
                .id(1L)
                .user(user)
                .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .gender(Gender.FEMALE)
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(profileRepo.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(profileRepo.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        ProfileResponse response = service.updateProfile(request, "testuser");

        // assert
        assertEquals(Gender.FEMALE, response.getGender());
    }

    @Test
    void updateProfile_shouldUpdateUnitPreference() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        UserProfile profile = UserProfile.builder()
                .id(1L)
                .user(user)
                .unitPreference(UnitPreference.IMPERIAL)
                .build();

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .unitPreference(UnitPreference.METRIC)
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(profileRepo.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(profileRepo.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        ProfileResponse response = service.updateProfile(request, "testuser");

        // assert
        assertEquals(UnitPreference.METRIC, response.getUnitPreference());
    }

    @Test
    void updateProfile_shouldCreateProfileIfNotExists() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .birthYear(2000)
                .gender(Gender.OTHER)
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(profileRepo.findByUserId(1L)).thenReturn(Optional.empty());
        when(profileRepo.save(any(UserProfile.class))).thenAnswer(invocation -> {
            UserProfile saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // act
        ProfileResponse response = service.updateProfile(request, "testuser");

        // assert
        assertEquals(2000, response.getBirthYear());
        assertEquals(Gender.OTHER, response.getGender());

        // save is called twice: once for creation, once for update
        verify(profileRepo, times(2)).save(any(UserProfile.class));
    }

    @Test
    void updateProfile_shouldNotOverwriteExistingValuesWithNull() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        UserProfile profile = UserProfile.builder()
                .id(1L)
                .user(user)
                .birthYear(1990)
                .gender(Gender.MALE)
                .unitPreference(UnitPreference.METRIC)
                .build();

        // Request with only birthYear - gender and unitPreference are null
        ProfileUpdateRequest request = ProfileUpdateRequest.builder()
                .birthYear(1991)
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(profileRepo.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(profileRepo.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        ProfileResponse response = service.updateProfile(request, "testuser");

        // assert
        assertEquals(1991, response.getBirthYear());
        assertEquals(Gender.MALE, response.getGender()); // unchanged
        assertEquals(UnitPreference.METRIC, response.getUnitPreference()); // unchanged
    }
}
