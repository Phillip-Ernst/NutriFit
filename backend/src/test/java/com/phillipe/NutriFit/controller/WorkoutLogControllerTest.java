package com.phillipe.NutriFit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phillipe.NutriFit.dto.request.ExerciseItemRequest;
import com.phillipe.NutriFit.dto.request.WorkoutLogFromPlanRequest;
import com.phillipe.NutriFit.dto.request.WorkoutLogRequest;
import com.phillipe.NutriFit.dto.response.WorkoutLogResponse;
import com.phillipe.NutriFit.config.SecurityConfig;
import com.phillipe.NutriFit.service.JwtService;
import com.phillipe.NutriFit.service.MyUserDetailsService;
import com.phillipe.NutriFit.service.UserService;
import com.phillipe.NutriFit.service.WorkoutLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkoutLogController.class)
@Import(SecurityConfig.class)
class WorkoutLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private WorkoutLogService workoutLogService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    // ==================== CREATE WORKOUT TESTS ====================

    @Test
    void createWorkout_success_shouldReturnWorkoutLogResponse() throws Exception {
        ExerciseItemRequest exercise = ExerciseItemRequest.builder()
                .name("Bench Press")
                .category("CHEST")
                .sets(3)
                .reps(10)
                .weight(135)
                .durationMinutes(15)
                .caloriesBurned(100)
                .build();

        WorkoutLogRequest request = WorkoutLogRequest.builder()
                .exercises(List.of(exercise))
                .build();

        WorkoutLogResponse response = WorkoutLogResponse.builder()
                .id(1L)
                .createdAt(Instant.now())
                .totalDurationMinutes(15)
                .totalCaloriesBurned(100)
                .totalSets(3)
                .totalReps(30)
                .exercises(List.of(exercise))
                .build();

        when(workoutLogService.createWorkout(any(WorkoutLogRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/workouts")
                        .with(csrf())
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.totalDurationMinutes").value(15))
                .andExpect(jsonPath("$.totalCaloriesBurned").value(100))
                .andExpect(jsonPath("$.exercises[0].name").value("Bench Press"));

        verify(workoutLogService).createWorkout(any(WorkoutLogRequest.class), eq("testuser"));
    }

    @Test
    void createWorkout_multipleExercises_shouldReturnAggregatedTotals() throws Exception {
        ExerciseItemRequest exercise1 = ExerciseItemRequest.builder()
                .name("Squats")
                .category("LEGS")
                .sets(4)
                .reps(12)
                .weight(185)
                .durationMinutes(20)
                .caloriesBurned(150)
                .build();

        ExerciseItemRequest exercise2 = ExerciseItemRequest.builder()
                .name("Leg Press")
                .category("LEGS")
                .sets(3)
                .reps(15)
                .weight(250)
                .durationMinutes(15)
                .caloriesBurned(120)
                .build();

        WorkoutLogRequest request = WorkoutLogRequest.builder()
                .exercises(List.of(exercise1, exercise2))
                .build();

        WorkoutLogResponse response = WorkoutLogResponse.builder()
                .id(2L)
                .createdAt(Instant.now())
                .totalDurationMinutes(35)
                .totalCaloriesBurned(270)
                .totalSets(7)
                .totalReps(93)
                .exercises(List.of(exercise1, exercise2))
                .build();

        when(workoutLogService.createWorkout(any(WorkoutLogRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/workouts")
                        .with(csrf())
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDurationMinutes").value(35))
                .andExpect(jsonPath("$.totalCaloriesBurned").value(270))
                .andExpect(jsonPath("$.exercises.length()").value(2));
    }

    @Test
    void createWorkout_emptyExercisesList_shouldReturnValidationError() throws Exception {
        WorkoutLogRequest request = WorkoutLogRequest.builder()
                .exercises(Collections.emptyList())
                .build();

        mockMvc.perform(post("/workouts")
                        .with(csrf())
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(workoutLogService, never()).createWorkout(any(), any());
    }

    @Test
    void createWorkout_exerciseWithoutName_shouldReturnValidationError() throws Exception {
        ExerciseItemRequest exercise = ExerciseItemRequest.builder()
                .name("")  // blank name should fail validation
                .sets(3)
                .reps(10)
                .build();

        WorkoutLogRequest request = WorkoutLogRequest.builder()
                .exercises(List.of(exercise))
                .build();

        mockMvc.perform(post("/workouts")
                        .with(csrf())
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(workoutLogService, never()).createWorkout(any(), any());
    }

    @Test
    void createWorkout_unauthenticated_shouldReturnForbidden() throws Exception {
        ExerciseItemRequest exercise = ExerciseItemRequest.builder()
                .name("Running")
                .durationMinutes(30)
                .build();

        WorkoutLogRequest request = WorkoutLogRequest.builder()
                .exercises(List.of(exercise))
                .build();

        mockMvc.perform(post("/workouts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ==================== CREATE WORKOUT FROM PLAN TESTS ====================

    @Test
    void createWorkoutFromPlan_success_shouldReturnCreatedWorkout() throws Exception {
        ExerciseItemRequest exercise = ExerciseItemRequest.builder()
                .name("Deadlift")
                .category("BACK")
                .sets(5)
                .reps(5)
                .weight(225)
                .durationMinutes(25)
                .caloriesBurned(200)
                .build();

        WorkoutLogFromPlanRequest request = WorkoutLogFromPlanRequest.builder()
                .workoutPlanDayId(1L)
                .exercises(List.of(exercise))
                .build();

        WorkoutLogResponse response = WorkoutLogResponse.builder()
                .id(3L)
                .createdAt(Instant.now())
                .workoutPlanDayId(1L)
                .workoutPlanDayName("Pull Day")
                .totalDurationMinutes(25)
                .totalCaloriesBurned(200)
                .totalSets(5)
                .totalReps(25)
                .exercises(List.of(exercise))
                .build();

        when(workoutLogService.createWorkoutFromPlan(any(WorkoutLogFromPlanRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/workouts/from-plan")
                        .with(csrf())
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.workoutPlanDayId").value(1L))
                .andExpect(jsonPath("$.workoutPlanDayName").value("Pull Day"));

        verify(workoutLogService).createWorkoutFromPlan(any(WorkoutLogFromPlanRequest.class), eq("testuser"));
    }

    @Test
    void createWorkoutFromPlan_nullPlanDayId_shouldReturnValidationError() throws Exception {
        ExerciseItemRequest exercise = ExerciseItemRequest.builder()
                .name("Pull-ups")
                .sets(3)
                .reps(8)
                .build();

        WorkoutLogFromPlanRequest request = WorkoutLogFromPlanRequest.builder()
                .workoutPlanDayId(null)
                .exercises(List.of(exercise))
                .build();

        mockMvc.perform(post("/workouts/from-plan")
                        .with(csrf())
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(workoutLogService, never()).createWorkoutFromPlan(any(), any());
    }

    @Test
    void createWorkoutFromPlan_emptyExercises_shouldReturnValidationError() throws Exception {
        WorkoutLogFromPlanRequest request = WorkoutLogFromPlanRequest.builder()
                .workoutPlanDayId(1L)
                .exercises(Collections.emptyList())
                .build();

        mockMvc.perform(post("/workouts/from-plan")
                        .with(csrf())
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(workoutLogService, never()).createWorkoutFromPlan(any(), any());
    }

    @Test
    void createWorkoutFromPlan_unauthenticated_shouldReturnForbidden() throws Exception {
        ExerciseItemRequest exercise = ExerciseItemRequest.builder()
                .name("Barbell Row")
                .sets(4)
                .reps(10)
                .build();

        WorkoutLogFromPlanRequest request = WorkoutLogFromPlanRequest.builder()
                .workoutPlanDayId(1L)
                .exercises(List.of(exercise))
                .build();

        mockMvc.perform(post("/workouts/from-plan")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ==================== GET MY WORKOUTS TESTS ====================

    @Test
    void getMyWorkouts_success_shouldReturnWorkoutsList() throws Exception {
        ExerciseItemRequest exercise = ExerciseItemRequest.builder()
                .name("Push-ups")
                .category("CHEST")
                .sets(3)
                .reps(20)
                .build();

        WorkoutLogResponse workout1 = WorkoutLogResponse.builder()
                .id(1L)
                .createdAt(Instant.now())
                .totalDurationMinutes(30)
                .totalCaloriesBurned(150)
                .totalSets(9)
                .totalReps(60)
                .exercises(List.of(exercise))
                .build();

        WorkoutLogResponse workout2 = WorkoutLogResponse.builder()
                .id(2L)
                .createdAt(Instant.now())
                .totalDurationMinutes(45)
                .totalCaloriesBurned(250)
                .totalSets(12)
                .totalReps(120)
                .exercises(List.of(exercise))
                .build();

        when(workoutLogService.getMyWorkouts("testuser")).thenReturn(List.of(workout1, workout2));

        mockMvc.perform(get("/workouts/mine")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(workoutLogService).getMyWorkouts("testuser");
    }

    @Test
    void getMyWorkouts_emptyList_shouldReturnEmptyArray() throws Exception {
        when(workoutLogService.getMyWorkouts("newuser")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/workouts/mine")
                        .with(user("newuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(workoutLogService).getMyWorkouts("newuser");
    }

    @Test
    void getMyWorkouts_unauthenticated_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/workouts/mine"))
                .andExpect(status().isForbidden());

        verify(workoutLogService, never()).getMyWorkouts(any());
    }

    @Test
    void getMyWorkouts_differentUser_shouldOnlyGetOwnWorkouts() throws Exception {
        when(workoutLogService.getMyWorkouts("user1")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/workouts/mine")
                        .with(user("user1")))
                .andExpect(status().isOk());

        verify(workoutLogService).getMyWorkouts("user1");
        verify(workoutLogService, never()).getMyWorkouts("user2");
    }
}
