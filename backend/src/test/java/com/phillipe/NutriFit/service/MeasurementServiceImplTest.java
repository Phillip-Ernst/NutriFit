package com.phillipe.NutriFit.service;

import com.phillipe.NutriFit.dto.request.MeasurementRequest;
import com.phillipe.NutriFit.dto.response.MeasurementResponse;
import com.phillipe.NutriFit.model.entity.BodyMeasurement;
import com.phillipe.NutriFit.model.entity.User;
import com.phillipe.NutriFit.repository.BodyMeasurementRepository;
import com.phillipe.NutriFit.repository.UserRepository;
import com.phillipe.NutriFit.service.impl.MeasurementServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeasurementServiceImplTest {

    @Mock
    private BodyMeasurementRepository measurementRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private ChangeHistoryService changeHistoryService;

    @InjectMocks
    private MeasurementServiceImpl service;

    @Test
    void createMeasurement_shouldPersistMeasurement() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        MeasurementRequest request = MeasurementRequest.builder()
                .heightCm(178.0)
                .weightKg(80.0)
                .bodyFatPercent(15.0)
                .chestCm(100.0)
                .bicepsCm(35.0)
                .waistCm(85.0)
                .notes("Morning measurement")
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(measurementRepo.save(any(BodyMeasurement.class))).thenAnswer(invocation -> {
            BodyMeasurement saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // act
        MeasurementResponse response = service.createMeasurement(request, "testuser");

        // assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(178.0, response.getHeightCm());
        assertEquals(80.0, response.getWeightKg());
        assertEquals(15.0, response.getBodyFatPercent());
        assertEquals(100.0, response.getChestCm());
        assertEquals(35.0, response.getBicepsCm());
        assertEquals(85.0, response.getWaistCm());
        assertEquals("Morning measurement", response.getNotes());

        ArgumentCaptor<BodyMeasurement> captor = ArgumentCaptor.forClass(BodyMeasurement.class);
        verify(measurementRepo).save(captor.capture());
        assertEquals(user, captor.getValue().getUser());
    }

    @Test
    void createMeasurement_shouldHandlePartialData() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        MeasurementRequest request = MeasurementRequest.builder()
                .weightKg(80.0)
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(measurementRepo.save(any(BodyMeasurement.class))).thenAnswer(invocation -> {
            BodyMeasurement saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // act
        MeasurementResponse response = service.createMeasurement(request, "testuser");

        // assert
        assertEquals(80.0, response.getWeightKg());
        assertNull(response.getHeightCm());
        assertNull(response.getBodyFatPercent());
        assertNull(response.getChestCm());
    }

    @Test
    void createMeasurement_shouldThrowWhenUserNotFound() {
        // arrange
        MeasurementRequest request = MeasurementRequest.builder()
                .weightKg(80.0)
                .build();

        when(userRepo.findByUsername("unknownuser")).thenReturn(null);

        // act & assert
        assertThrows(UsernameNotFoundException.class,
                () -> service.createMeasurement(request, "unknownuser"));

        verifyNoInteractions(measurementRepo);
    }

    @Test
    void getMeasurements_shouldReturnUserMeasurements() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        BodyMeasurement m1 = BodyMeasurement.builder()
                .id(1L)
                .user(user)
                .recordedAt(Instant.now())
                .heightCm(178.0)
                .weightKg(80.0)
                .build();

        BodyMeasurement m2 = BodyMeasurement.builder()
                .id(2L)
                .user(user)
                .recordedAt(Instant.now().minusSeconds(86400))
                .heightCm(178.0)
                .weightKg(81.0)
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(measurementRepo.findByUserIdOrderByRecordedAtDesc(1L))
                .thenReturn(List.of(m1, m2));

        // act
        List<MeasurementResponse> responses = service.getMeasurements("testuser");

        // assert
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals(2L, responses.get(1).getId());
        assertEquals(80.0, responses.get(0).getWeightKg());
        assertEquals(81.0, responses.get(1).getWeightKg());
    }

    @Test
    void getMeasurements_shouldReturnEmptyListWhenNoMeasurements() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(measurementRepo.findByUserIdOrderByRecordedAtDesc(1L))
                .thenReturn(Collections.emptyList());

        // act
        List<MeasurementResponse> responses = service.getMeasurements("testuser");

        // assert
        assertTrue(responses.isEmpty());
    }

    @Test
    void getLatestMeasurement_shouldReturnMostRecent() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        BodyMeasurement latest = BodyMeasurement.builder()
                .id(1L)
                .user(user)
                .recordedAt(Instant.now())
                .heightCm(178.0)
                .weightKg(80.0)
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(measurementRepo.findFirstByUserIdOrderByRecordedAtDesc(1L))
                .thenReturn(Optional.of(latest));

        // act
        MeasurementResponse response = service.getLatestMeasurement("testuser");

        // assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(80.0, response.getWeightKg());
    }

    @Test
    void getLatestMeasurement_shouldReturnNullWhenNoMeasurements() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(measurementRepo.findFirstByUserIdOrderByRecordedAtDesc(1L))
                .thenReturn(Optional.empty());

        // act
        MeasurementResponse response = service.getLatestMeasurement("testuser");

        // assert
        assertNull(response);
    }

    @Test
    void deleteMeasurement_shouldDeleteOwnedMeasurement() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        BodyMeasurement measurement = BodyMeasurement.builder()
                .id(1L)
                .user(user)
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(measurementRepo.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(measurement));

        // act
        service.deleteMeasurement(1L, "testuser");

        // assert
        verify(measurementRepo).delete(measurement);
    }

    @Test
    void deleteMeasurement_shouldThrowWhenMeasurementNotFound() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(measurementRepo.findByIdAndUserId(999L, 1L))
                .thenReturn(Optional.empty());

        // act & assert
        assertThrows(IllegalArgumentException.class,
                () -> service.deleteMeasurement(999L, "testuser"));

        verify(measurementRepo, never()).delete(any());
    }

    @Test
    void deleteMeasurement_shouldNotDeleteOtherUsersMeasurement() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        // Measurement belongs to user 2, but testuser (id=1) is trying to delete it
        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(measurementRepo.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.empty()); // Not found for this user

        // act & assert
        assertThrows(IllegalArgumentException.class,
                () -> service.deleteMeasurement(1L, "testuser"));

        verify(measurementRepo, never()).delete(any());
    }
}
