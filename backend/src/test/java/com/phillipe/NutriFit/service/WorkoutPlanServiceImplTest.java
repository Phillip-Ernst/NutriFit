package com.phillipe.NutriFit.service;

import com.phillipe.NutriFit.repository.UserRepository;
import com.phillipe.NutriFit.model.entity.User;
import com.phillipe.NutriFit.repository.WorkoutPlanDayRepository;
import com.phillipe.NutriFit.repository.WorkoutPlanRepository;
import com.phillipe.NutriFit.dto.request.WorkoutPlanDayRequest;
import com.phillipe.NutriFit.dto.request.WorkoutPlanExerciseRequest;
import com.phillipe.NutriFit.dto.request.WorkoutPlanRequest;
import com.phillipe.NutriFit.dto.response.PredefinedExerciseResponse;
import com.phillipe.NutriFit.dto.response.WorkoutPlanDayResponse;
import com.phillipe.NutriFit.dto.response.WorkoutPlanResponse;
import com.phillipe.NutriFit.model.*;
import com.phillipe.NutriFit.model.embedded.WorkoutPlanExercise;
import com.phillipe.NutriFit.model.entity.WorkoutPlan;
import com.phillipe.NutriFit.model.entity.WorkoutPlanDay;
import com.phillipe.NutriFit.service.impl.WorkoutPlanServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutPlanServiceImplTest {

    @Mock
    private WorkoutPlanRepository workoutPlanRepo;

    @Mock
    private WorkoutPlanDayRepository workoutPlanDayRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private WorkoutPlanServiceImpl service;

    @Test
    void createPlan_shouldPersistPlanWithDaysAndExercises() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        WorkoutPlanExerciseRequest exerciseReq = WorkoutPlanExerciseRequest.builder()
                .name("Bench Press")
                .category(ExerciseCategory.CHEST)
                .isCustom(false)
                .targetSets(3)
                .targetReps(10)
                .targetWeight(135)
                .build();

        WorkoutPlanDayRequest dayReq = WorkoutPlanDayRequest.builder()
                .dayNumber(1)
                .dayName("Push Day")
                .exercises(List.of(exerciseReq))
                .build();

        WorkoutPlanRequest request = WorkoutPlanRequest.builder()
                .name("PPL Split")
                .description("Push Pull Legs")
                .days(List.of(dayReq))
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutPlanRepo.save(any(WorkoutPlan.class))).thenAnswer(invocation -> {
            WorkoutPlan saved = invocation.getArgument(0);
            saved.setId(1L);
            if (!saved.getDays().isEmpty()) {
                saved.getDays().get(0).setId(10L);
            }
            return saved;
        });

        // act
        WorkoutPlanResponse response = service.createPlan(request, "testuser");

        // assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("PPL Split", response.getName());
        assertEquals("Push Pull Legs", response.getDescription());
        assertEquals(1, response.getDays().size());

        WorkoutPlanDayResponse dayResp = response.getDays().get(0);
        assertEquals(10L, dayResp.getId());
        assertEquals(1, dayResp.getDayNumber());
        assertEquals("Push Day", dayResp.getDayName());
        assertEquals(1, dayResp.getExercises().size());
        assertEquals("Bench Press", dayResp.getExercises().get(0).getName());
        assertEquals(ExerciseCategory.CHEST, dayResp.getExercises().get(0).getCategory());

        ArgumentCaptor<WorkoutPlan> captor = ArgumentCaptor.forClass(WorkoutPlan.class);
        verify(workoutPlanRepo).save(captor.capture());
        WorkoutPlan savedPlan = captor.getValue();
        assertEquals(user, savedPlan.getUser());
    }

    @Test
    void createPlan_shouldThrowWhenUserNotFound() {
        // arrange
        WorkoutPlanRequest request = WorkoutPlanRequest.builder()
                .name("Test Plan")
                .days(List.of(WorkoutPlanDayRequest.builder()
                        .dayNumber(1)
                        .dayName("Day 1")
                        .build()))
                .build();

        when(userRepo.findByUsername("unknownuser")).thenReturn(null);

        // act & assert
        assertThrows(UsernameNotFoundException.class,
                () -> service.createPlan(request, "unknownuser"));

        verify(userRepo).findByUsername("unknownuser");
        verifyNoInteractions(workoutPlanRepo);
    }

    @Test
    void getMyPlans_shouldReturnUserPlans() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        WorkoutPlan plan1 = WorkoutPlan.builder()
                .id(1L)
                .user(user)
                .name("Plan A")
                .createdAt(Instant.now())
                .days(new ArrayList<>())
                .build();

        WorkoutPlan plan2 = WorkoutPlan.builder()
                .id(2L)
                .user(user)
                .name("Plan B")
                .createdAt(Instant.now().minusSeconds(3600))
                .days(new ArrayList<>())
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutPlanRepo.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(plan1, plan2));

        // act
        List<WorkoutPlanResponse> responses = service.getMyPlans("testuser");

        // assert
        assertEquals(2, responses.size());
        assertEquals("Plan A", responses.get(0).getName());
        assertEquals("Plan B", responses.get(1).getName());
    }

    @Test
    void getMyPlans_shouldReturnEmptyListWhenNoPlans() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutPlanRepo.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(Collections.emptyList());

        // act
        List<WorkoutPlanResponse> responses = service.getMyPlans("testuser");

        // assert
        assertTrue(responses.isEmpty());
    }

    @Test
    void getPlanById_shouldReturnPlanWhenFound() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        WorkoutPlan plan = WorkoutPlan.builder()
                .id(1L)
                .user(user)
                .name("Test Plan")
                .createdAt(Instant.now())
                .days(new ArrayList<>())
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutPlanRepo.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(plan));

        // act
        WorkoutPlanResponse response = service.getPlanById(1L, "testuser");

        // assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Plan", response.getName());
    }

    @Test
    void getPlanById_shouldThrowWhenPlanNotFound() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutPlanRepo.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(EntityNotFoundException.class,
                () -> service.getPlanById(999L, "testuser"));
    }

    @Test
    void updatePlan_shouldUpdatePlanDetails() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        WorkoutPlan existingPlan = WorkoutPlan.builder()
                .id(1L)
                .user(user)
                .name("Old Name")
                .description("Old Desc")
                .createdAt(Instant.now())
                .days(new ArrayList<>())
                .build();

        WorkoutPlanRequest request = WorkoutPlanRequest.builder()
                .name("New Name")
                .description("New Desc")
                .days(List.of(WorkoutPlanDayRequest.builder()
                        .dayNumber(1)
                        .dayName("New Day")
                        .build()))
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutPlanRepo.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(existingPlan));
        when(workoutPlanRepo.save(any(WorkoutPlan.class))).thenAnswer(invocation -> {
            WorkoutPlan saved = invocation.getArgument(0);
            if (!saved.getDays().isEmpty()) {
                saved.getDays().get(0).setId(20L);
            }
            return saved;
        });

        // act
        WorkoutPlanResponse response = service.updatePlan(1L, request, "testuser");

        // assert
        assertEquals("New Name", response.getName());
        assertEquals("New Desc", response.getDescription());
        assertEquals(1, response.getDays().size());
        assertEquals("New Day", response.getDays().get(0).getDayName());
    }

    @Test
    void deletePlan_shouldDeletePlanWhenFound() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        WorkoutPlan plan = WorkoutPlan.builder()
                .id(1L)
                .user(user)
                .name("To Delete")
                .days(new ArrayList<>())
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutPlanRepo.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(plan));

        // act
        service.deletePlan(1L, "testuser");

        // assert
        verify(workoutPlanRepo).delete(plan);
    }

    @Test
    void deletePlan_shouldThrowWhenPlanNotFound() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutPlanRepo.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(EntityNotFoundException.class,
                () -> service.deletePlan(999L, "testuser"));

        verify(workoutPlanRepo, never()).delete(any());
    }

    @Test
    void getPlanDayById_shouldReturnDayWhenFound() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        WorkoutPlan plan = WorkoutPlan.builder()
                .id(1L)
                .user(user)
                .name("Test Plan")
                .build();

        WorkoutPlanExercise exercise = WorkoutPlanExercise.builder()
                .name("Squats")
                .category(ExerciseCategory.QUADS)
                .targetSets(4)
                .targetReps(8)
                .build();

        WorkoutPlanDay day = WorkoutPlanDay.builder()
                .id(10L)
                .workoutPlan(plan)
                .dayNumber(1)
                .dayName("Leg Day")
                .exercises(List.of(exercise))
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutPlanDayRepo.findByIdAndWorkoutPlanUserId(10L, 1L))
                .thenReturn(Optional.of(day));

        // act
        WorkoutPlanDayResponse response = service.getPlanDayById(10L, "testuser");

        // assert
        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Leg Day", response.getDayName());
        assertEquals(1, response.getExercises().size());
        assertEquals("Squats", response.getExercises().get(0).getName());
    }

    @Test
    void getPlanDayById_shouldThrowWhenDayNotFound() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(workoutPlanDayRepo.findByIdAndWorkoutPlanUserId(999L, 1L))
                .thenReturn(Optional.empty());

        // act & assert
        assertThrows(EntityNotFoundException.class,
                () -> service.getPlanDayById(999L, "testuser"));
    }

    @Test
    void getPredefinedExercises_shouldReturnAllExercises() {
        // act
        List<PredefinedExerciseResponse> exercises = service.getPredefinedExercises(null);

        // assert
        assertFalse(exercises.isEmpty());
        assertEquals(PredefinedExercise.values().length, exercises.size());

        // verify structure
        PredefinedExerciseResponse first = exercises.get(0);
        assertNotNull(first.getId());
        assertNotNull(first.getName());
        assertNotNull(first.getCategory());
    }

    @Test
    void getPredefinedExercises_shouldFilterByCategory() {
        // act
        List<PredefinedExerciseResponse> chestExercises =
                service.getPredefinedExercises(ExerciseCategory.CHEST);

        // assert
        assertFalse(chestExercises.isEmpty());
        assertTrue(chestExercises.stream()
                .allMatch(e -> e.getCategory() == ExerciseCategory.CHEST));
    }

    @Test
    void getCategories_shouldReturnAllCategories() {
        // act
        List<ExerciseCategory> categories = service.getCategories();

        // assert
        assertEquals(ExerciseCategory.values().length, categories.size());
        assertTrue(categories.contains(ExerciseCategory.CHEST));
        assertTrue(categories.contains(ExerciseCategory.BACK));
        assertTrue(categories.contains(ExerciseCategory.CARDIO));
    }
}
