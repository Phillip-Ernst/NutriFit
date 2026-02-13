package com.phillipe.NutriFit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phillipe.NutriFit.dto.request.WorkoutPlanDayRequest;
import com.phillipe.NutriFit.dto.request.WorkoutPlanExerciseRequest;
import com.phillipe.NutriFit.dto.request.WorkoutPlanRequest;
import com.phillipe.NutriFit.dto.response.WorkoutPlanDayResponse;
import com.phillipe.NutriFit.dto.response.WorkoutPlanExerciseResponse;
import com.phillipe.NutriFit.dto.response.WorkoutPlanResponse;
import com.phillipe.NutriFit.model.ExerciseCategory;
import com.phillipe.NutriFit.config.RateLimitConfig;
import com.phillipe.NutriFit.config.SecurityConfig;
import com.phillipe.NutriFit.service.JwtService;
import com.phillipe.NutriFit.service.MyUserDetailsService;
import com.phillipe.NutriFit.service.UserService;
import com.phillipe.NutriFit.service.WorkoutPlanService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkoutPlanController.class)
@Import(SecurityConfig.class)
class WorkoutPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private WorkoutPlanService workoutPlanService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @MockitoBean
    private RateLimitConfig rateLimitConfig;

    // ==================== CREATE PLAN TESTS ====================

    @Test
    void createPlan_success_shouldReturnCreatedPlan() throws Exception {
        WorkoutPlanExerciseRequest exerciseReq = WorkoutPlanExerciseRequest.builder()
                .name("Bench Press")
                .category(ExerciseCategory.CHEST)
                .targetSets(4)
                .targetReps(8)
                .targetWeight(135)
                .build();

        WorkoutPlanDayRequest dayReq = WorkoutPlanDayRequest.builder()
                .dayNumber(1)
                .dayName("Push Day")
                .exercises(List.of(exerciseReq))
                .build();

        WorkoutPlanRequest request = WorkoutPlanRequest.builder()
                .name("PPL Split")
                .description("Push Pull Legs routine")
                .days(List.of(dayReq))
                .build();

        WorkoutPlanExerciseResponse exerciseRes = WorkoutPlanExerciseResponse.builder()
                .name("Bench Press")
                .category(ExerciseCategory.CHEST)
                .targetSets(4)
                .targetReps(8)
                .targetWeight(135)
                .build();

        WorkoutPlanDayResponse dayRes = WorkoutPlanDayResponse.builder()
                .id(1L)
                .dayNumber(1)
                .dayName("Push Day")
                .exercises(List.of(exerciseRes))
                .build();

        WorkoutPlanResponse response = WorkoutPlanResponse.builder()
                .id(1L)
                .name("PPL Split")
                .description("Push Pull Legs routine")
                .createdAt(Instant.now())
                .days(List.of(dayRes))
                .build();

        when(workoutPlanService.createPlan(any(WorkoutPlanRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/workout-plans")
                        .with(csrf())
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("PPL Split"))
                .andExpect(jsonPath("$.days[0].dayName").value("Push Day"));

        verify(workoutPlanService).createPlan(any(WorkoutPlanRequest.class), eq("testuser"));
    }

    @Test
    void createPlan_blankName_shouldReturnValidationError() throws Exception {
        WorkoutPlanDayRequest dayReq = WorkoutPlanDayRequest.builder()
                .dayNumber(1)
                .dayName("Day 1")
                .build();

        WorkoutPlanRequest request = WorkoutPlanRequest.builder()
                .name("")  // blank name
                .days(List.of(dayReq))
                .build();

        mockMvc.perform(post("/workout-plans")
                        .with(csrf())
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(workoutPlanService, never()).createPlan(any(), any());
    }

    @Test
    void createPlan_emptyDays_shouldReturnValidationError() throws Exception {
        WorkoutPlanRequest request = WorkoutPlanRequest.builder()
                .name("My Plan")
                .days(Collections.emptyList())
                .build();

        mockMvc.perform(post("/workout-plans")
                        .with(csrf())
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(workoutPlanService, never()).createPlan(any(), any());
    }

    @Test
    void createPlan_unauthenticated_shouldReturnForbidden() throws Exception {
        WorkoutPlanDayRequest dayReq = WorkoutPlanDayRequest.builder()
                .dayNumber(1)
                .dayName("Day 1")
                .build();

        WorkoutPlanRequest request = WorkoutPlanRequest.builder()
                .name("My Plan")
                .days(List.of(dayReq))
                .build();

        mockMvc.perform(post("/workout-plans")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ==================== GET MY PLANS TESTS ====================

    @Test
    void getMyPlans_success_shouldReturnPlansList() throws Exception {
        WorkoutPlanResponse plan1 = WorkoutPlanResponse.builder()
                .id(1L)
                .name("Plan A")
                .createdAt(Instant.now())
                .days(Collections.emptyList())
                .build();

        WorkoutPlanResponse plan2 = WorkoutPlanResponse.builder()
                .id(2L)
                .name("Plan B")
                .createdAt(Instant.now())
                .days(Collections.emptyList())
                .build();

        when(workoutPlanService.getMyPlans("testuser")).thenReturn(List.of(plan1, plan2));

        mockMvc.perform(get("/workout-plans/mine")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Plan A"))
                .andExpect(jsonPath("$[1].name").value("Plan B"));

        verify(workoutPlanService).getMyPlans("testuser");
    }

    @Test
    void getMyPlans_emptyList_shouldReturnEmptyArray() throws Exception {
        when(workoutPlanService.getMyPlans("newuser")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/workout-plans/mine")
                        .with(user("newuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getMyPlans_unauthenticated_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/workout-plans/mine"))
                .andExpect(status().isForbidden());
    }

    // ==================== GET PLAN BY ID TESTS ====================

    @Test
    void getPlanById_success_shouldReturnPlan() throws Exception {
        WorkoutPlanResponse response = WorkoutPlanResponse.builder()
                .id(1L)
                .name("My Plan")
                .description("Test plan")
                .createdAt(Instant.now())
                .days(Collections.emptyList())
                .build();

        when(workoutPlanService.getPlanById(1L, "testuser")).thenReturn(response);

        mockMvc.perform(get("/workout-plans/1")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("My Plan"));

        verify(workoutPlanService).getPlanById(1L, "testuser");
    }

    @Test
    void getPlanById_notFound_shouldReturn404() throws Exception {
        when(workoutPlanService.getPlanById(999L, "testuser"))
                .thenThrow(new EntityNotFoundException("Plan not found"));

        mockMvc.perform(get("/workout-plans/999")
                        .with(user("testuser")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPlanById_wrongUser_shouldReturn403() throws Exception {
        when(workoutPlanService.getPlanById(1L, "wronguser"))
                .thenThrow(new AccessDeniedException("You don't have access to this plan"));

        mockMvc.perform(get("/workout-plans/1")
                        .with(user("wronguser")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPlanById_unauthenticated_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/workout-plans/1"))
                .andExpect(status().isForbidden());
    }

    // ==================== UPDATE PLAN TESTS ====================

    @Test
    void updatePlan_success_shouldReturnUpdatedPlan() throws Exception {
        WorkoutPlanDayRequest dayReq = WorkoutPlanDayRequest.builder()
                .dayNumber(1)
                .dayName("Updated Day")
                .build();

        WorkoutPlanRequest request = WorkoutPlanRequest.builder()
                .name("Updated Plan")
                .description("Updated description")
                .days(List.of(dayReq))
                .build();

        WorkoutPlanDayResponse dayRes = WorkoutPlanDayResponse.builder()
                .id(1L)
                .dayNumber(1)
                .dayName("Updated Day")
                .exercises(Collections.emptyList())
                .build();

        WorkoutPlanResponse response = WorkoutPlanResponse.builder()
                .id(1L)
                .name("Updated Plan")
                .description("Updated description")
                .createdAt(Instant.now())
                .days(List.of(dayRes))
                .build();

        when(workoutPlanService.updatePlan(eq(1L), any(WorkoutPlanRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(put("/workout-plans/1")
                        .with(csrf())
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Plan"))
                .andExpect(jsonPath("$.description").value("Updated description"));

        verify(workoutPlanService).updatePlan(eq(1L), any(WorkoutPlanRequest.class), eq("testuser"));
    }

    @Test
    void updatePlan_notFound_shouldReturn404() throws Exception {
        WorkoutPlanDayRequest dayReq = WorkoutPlanDayRequest.builder()
                .dayNumber(1)
                .dayName("Day")
                .build();

        WorkoutPlanRequest request = WorkoutPlanRequest.builder()
                .name("Plan")
                .days(List.of(dayReq))
                .build();

        when(workoutPlanService.updatePlan(eq(999L), any(WorkoutPlanRequest.class), eq("testuser")))
                .thenThrow(new EntityNotFoundException("Plan not found"));

        mockMvc.perform(put("/workout-plans/999")
                        .with(csrf())
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePlan_blankName_shouldReturnValidationError() throws Exception {
        WorkoutPlanDayRequest dayReq = WorkoutPlanDayRequest.builder()
                .dayNumber(1)
                .dayName("Day")
                .build();

        WorkoutPlanRequest request = WorkoutPlanRequest.builder()
                .name("")  // blank name
                .days(List.of(dayReq))
                .build();

        mockMvc.perform(put("/workout-plans/1")
                        .with(csrf())
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(workoutPlanService, never()).updatePlan(any(), any(), any());
    }

    @Test
    void updatePlan_unauthenticated_shouldReturnForbidden() throws Exception {
        WorkoutPlanDayRequest dayReq = WorkoutPlanDayRequest.builder()
                .dayNumber(1)
                .dayName("Day")
                .build();

        WorkoutPlanRequest request = WorkoutPlanRequest.builder()
                .name("Plan")
                .days(List.of(dayReq))
                .build();

        mockMvc.perform(put("/workout-plans/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ==================== DELETE PLAN TESTS ====================

    @Test
    void deletePlan_success_shouldReturnNoContent() throws Exception {
        doNothing().when(workoutPlanService).deletePlan(1L, "testuser");

        mockMvc.perform(delete("/workout-plans/1")
                        .with(csrf())
                        .with(user("testuser")))
                .andExpect(status().isNoContent());

        verify(workoutPlanService).deletePlan(1L, "testuser");
    }

    @Test
    void deletePlan_notFound_shouldReturn404() throws Exception {
        doThrow(new EntityNotFoundException("Plan not found"))
                .when(workoutPlanService).deletePlan(999L, "testuser");

        mockMvc.perform(delete("/workout-plans/999")
                        .with(csrf())
                        .with(user("testuser")))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePlan_wrongUser_shouldReturn403() throws Exception {
        doThrow(new AccessDeniedException("You don't have access to this plan"))
                .when(workoutPlanService).deletePlan(1L, "wronguser");

        mockMvc.perform(delete("/workout-plans/1")
                        .with(csrf())
                        .with(user("wronguser")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletePlan_unauthenticated_shouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/workout-plans/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    // ==================== GET PLAN DAY BY ID TESTS ====================

    @Test
    void getPlanDayById_success_shouldReturnPlanDay() throws Exception {
        WorkoutPlanExerciseResponse exerciseRes = WorkoutPlanExerciseResponse.builder()
                .name("Squat")
                .category(ExerciseCategory.QUADS)
                .targetSets(5)
                .targetReps(5)
                .build();

        WorkoutPlanDayResponse response = WorkoutPlanDayResponse.builder()
                .id(1L)
                .dayNumber(1)
                .dayName("Leg Day")
                .exercises(List.of(exerciseRes))
                .build();

        when(workoutPlanService.getPlanDayById(1L, "testuser")).thenReturn(response);

        mockMvc.perform(get("/workout-plans/days/1")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.dayName").value("Leg Day"))
                .andExpect(jsonPath("$.exercises[0].name").value("Squat"));

        verify(workoutPlanService).getPlanDayById(1L, "testuser");
    }

    @Test
    void getPlanDayById_notFound_shouldReturn404() throws Exception {
        when(workoutPlanService.getPlanDayById(999L, "testuser"))
                .thenThrow(new EntityNotFoundException("Plan day not found"));

        mockMvc.perform(get("/workout-plans/days/999")
                        .with(user("testuser")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPlanDayById_unauthenticated_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/workout-plans/days/1"))
                .andExpect(status().isForbidden());
    }
}
