package com.phillipe.NutriFit.service;

import com.phillipe.NutriFit.repository.UserRepository;
import com.phillipe.NutriFit.model.entity.User;
import com.phillipe.NutriFit.repository.WorkoutLogRepository;
import com.phillipe.NutriFit.dto.request.ExerciseItemRequest;
import com.phillipe.NutriFit.dto.request.WorkoutLogRequest;
import com.phillipe.NutriFit.dto.response.WorkoutLogResponse;
import com.phillipe.NutriFit.model.entity.WorkoutLog;
import com.phillipe.NutriFit.repository.WorkoutPlanDayRepository;
import com.phillipe.NutriFit.dto.request.WorkoutLogFromPlanRequest;
import com.phillipe.NutriFit.model.entity.WorkoutPlan;
import com.phillipe.NutriFit.model.entity.WorkoutPlanDay;
import com.phillipe.NutriFit.service.impl.WorkoutLogServiceImpl;
import jakarta.persistence.EntityNotFoundException;
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
class WorkoutLogServiceImplTest {

    @Mock
    private WorkoutLogRepository workoutLogRepo;

    @Mock
    private WorkoutPlanDayRepository workoutPlanDayRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private WorkoutLogServiceImpl service;

    @Test
    void createWorkout_shouldPersistWorkoutForUser() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        ExerciseItemRequest exercise = ExerciseItemRequest.builder()
                .name("Bench Press")
                .category("strength")
                .sets(3)
                .reps(10)
                .weight(135)
                .durationMinutes(15)
                .caloriesBurned(100)
                .build();

        WorkoutLogRequest request = WorkoutLogRequest.builder()
                .exercises(List.of(exercise))
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutLogRepo.save(any(WorkoutLog.class))).thenAnswer(invocation -> {
            WorkoutLog saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // act
        WorkoutLogResponse response = service.createWorkout(request, "testuser");

        // assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(15, response.getTotalDurationMinutes());
        assertEquals(100, response.getTotalCaloriesBurned());
        assertEquals(3, response.getTotalSets());
        assertEquals(10, response.getTotalReps());
        assertEquals(1, response.getExercises().size());
        assertEquals("Bench Press", response.getExercises().get(0).getName());

        verify(userRepo).findByUsername("testuser");
        verify(workoutLogRepo).save(any(WorkoutLog.class));
    }

    @Test
    void createWorkout_shouldThrowWhenUserNotFound() {
        // arrange
        WorkoutLogRequest request = WorkoutLogRequest.builder()
                .exercises(List.of(ExerciseItemRequest.builder().name("Running").build()))
                .build();

        when(userRepo.findByUsername("unknownuser")).thenReturn(null);

        // act & assert
        assertThrows(UsernameNotFoundException.class,
                () -> service.createWorkout(request, "unknownuser"));

        verify(userRepo).findByUsername("unknownuser");
        verifyNoInteractions(workoutLogRepo);
    }

    @Test
    void createWorkout_shouldTreatNullValuesAsZero() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        ExerciseItemRequest exercise = ExerciseItemRequest.builder()
                .name("Stretching")
                .category(null)
                .sets(null)
                .reps(null)
                .weight(null)
                .durationMinutes(null)
                .caloriesBurned(null)
                .build();

        WorkoutLogRequest request = WorkoutLogRequest.builder()
                .exercises(List.of(exercise))
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutLogRepo.save(any(WorkoutLog.class))).thenAnswer(invocation -> {
            WorkoutLog saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // act
        WorkoutLogResponse response = service.createWorkout(request, "testuser");

        // assert
        assertEquals(0, response.getTotalDurationMinutes());
        assertEquals(0, response.getTotalCaloriesBurned());
        assertEquals(0, response.getTotalSets());
        assertEquals(0, response.getTotalReps());
    }

    @Test
    void createWorkout_shouldAggregateMultipleExercises() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        ExerciseItemRequest exercise1 = ExerciseItemRequest.builder()
                .name("Bench Press")
                .sets(3)
                .reps(10)
                .durationMinutes(15)
                .caloriesBurned(100)
                .build();

        ExerciseItemRequest exercise2 = ExerciseItemRequest.builder()
                .name("Squats")
                .sets(4)
                .reps(8)
                .durationMinutes(20)
                .caloriesBurned(150)
                .build();

        WorkoutLogRequest request = WorkoutLogRequest.builder()
                .exercises(List.of(exercise1, exercise2))
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutLogRepo.save(any(WorkoutLog.class))).thenAnswer(invocation -> {
            WorkoutLog saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // act
        WorkoutLogResponse response = service.createWorkout(request, "testuser");

        // assert
        assertEquals(35, response.getTotalDurationMinutes()); // 15 + 20
        assertEquals(250, response.getTotalCaloriesBurned()); // 100 + 150
        assertEquals(7, response.getTotalSets()); // 3 + 4
        assertEquals(18, response.getTotalReps()); // 10 + 8
        assertEquals(2, response.getExercises().size());

        ArgumentCaptor<WorkoutLog> captor = ArgumentCaptor.forClass(WorkoutLog.class);
        verify(workoutLogRepo).save(captor.capture());
        WorkoutLog savedWorkout = captor.getValue();
        assertEquals(user, savedWorkout.getUser());
        assertEquals(2, savedWorkout.getExercises().size());
    }

    @Test
    void getMyWorkouts_shouldReturnUserWorkouts() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        WorkoutLog workout1 = WorkoutLog.builder()
                .id(1L)
                .user(user)
                .createdAt(Instant.now())
                .totalDurationMinutes(30)
                .totalCaloriesBurned(200)
                .totalSets(10)
                .totalReps(100)
                .build();

        WorkoutLog workout2 = WorkoutLog.builder()
                .id(2L)
                .user(user)
                .createdAt(Instant.now().minusSeconds(3600))
                .totalDurationMinutes(45)
                .totalCaloriesBurned(300)
                .totalSets(12)
                .totalReps(120)
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutLogRepo.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(workout1, workout2));

        // act
        List<WorkoutLogResponse> responses = service.getMyWorkouts("testuser");

        // assert
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals(2L, responses.get(1).getId());

        verify(userRepo).findByUsername("testuser");
        verify(workoutLogRepo).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getMyWorkouts_shouldReturnEmptyListWhenNoWorkouts() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutLogRepo.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(Collections.emptyList());

        // act
        List<WorkoutLogResponse> responses = service.getMyWorkouts("testuser");

        // assert
        assertTrue(responses.isEmpty());

        verify(userRepo).findByUsername("testuser");
        verify(workoutLogRepo).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getMyWorkouts_shouldThrowWhenUserNotFound() {
        // arrange
        when(userRepo.findByUsername("unknownuser")).thenReturn(null);

        // act & assert
        assertThrows(UsernameNotFoundException.class,
                () -> service.getMyWorkouts("unknownuser"));

        verify(userRepo).findByUsername("unknownuser");
        verifyNoInteractions(workoutLogRepo);
    }

    @Test
    void createWorkoutFromPlan_shouldPersistWorkoutWithPlanDay() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        WorkoutPlan plan = WorkoutPlan.builder()
                .id(1L)
                .user(user)
                .name("Test Plan")
                .build();

        WorkoutPlanDay planDay = WorkoutPlanDay.builder()
                .id(10L)
                .workoutPlan(plan)
                .dayNumber(1)
                .dayName("Push Day")
                .build();

        ExerciseItemRequest exercise = ExerciseItemRequest.builder()
                .name("Bench Press")
                .category("CHEST")
                .sets(3)
                .reps(10)
                .weight(135)
                .durationMinutes(15)
                .caloriesBurned(100)
                .build();

        WorkoutLogFromPlanRequest request = WorkoutLogFromPlanRequest.builder()
                .workoutPlanDayId(10L)
                .exercises(List.of(exercise))
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutPlanDayRepo.findByIdAndWorkoutPlanUserId(10L, 1L))
                .thenReturn(Optional.of(planDay));
        when(workoutLogRepo.save(any(WorkoutLog.class))).thenAnswer(invocation -> {
            WorkoutLog saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // act
        WorkoutLogResponse response = service.createWorkoutFromPlan(request, "testuser");

        // assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(10L, response.getWorkoutPlanDayId());
        assertEquals("Push Day", response.getWorkoutPlanDayName());
        assertEquals(15, response.getTotalDurationMinutes());
        assertEquals(100, response.getTotalCaloriesBurned());
        assertEquals(3, response.getTotalSets());
        assertEquals(10, response.getTotalReps());
        assertEquals(1, response.getExercises().size());

        ArgumentCaptor<WorkoutLog> captor = ArgumentCaptor.forClass(WorkoutLog.class);
        verify(workoutLogRepo).save(captor.capture());
        WorkoutLog savedWorkout = captor.getValue();
        assertEquals(planDay, savedWorkout.getWorkoutPlanDay());
    }

    @Test
    void createWorkoutFromPlan_shouldThrowWhenPlanDayNotFound() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        WorkoutLogFromPlanRequest request = WorkoutLogFromPlanRequest.builder()
                .workoutPlanDayId(999L)
                .exercises(List.of(ExerciseItemRequest.builder().name("Squats").build()))
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutPlanDayRepo.findByIdAndWorkoutPlanUserId(999L, 1L))
                .thenReturn(Optional.empty());

        // act & assert
        assertThrows(EntityNotFoundException.class,
                () -> service.createWorkoutFromPlan(request, "testuser"));

        verify(workoutPlanDayRepo).findByIdAndWorkoutPlanUserId(999L, 1L);
        verifyNoInteractions(workoutLogRepo);
    }

    @Test
    void createWorkoutFromPlan_shouldThrowWhenUserNotFound() {
        // arrange
        WorkoutLogFromPlanRequest request = WorkoutLogFromPlanRequest.builder()
                .workoutPlanDayId(10L)
                .exercises(List.of(ExerciseItemRequest.builder().name("Running").build()))
                .build();

        when(userRepo.findByUsername("unknownuser")).thenReturn(null);

        // act & assert
        assertThrows(UsernameNotFoundException.class,
                () -> service.createWorkoutFromPlan(request, "unknownuser"));

        verify(userRepo).findByUsername("unknownuser");
        verifyNoInteractions(workoutPlanDayRepo);
        verifyNoInteractions(workoutLogRepo);
    }
}
