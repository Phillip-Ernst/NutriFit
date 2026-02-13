package com.phillipe.NutriFit.controller;

import com.phillipe.NutriFit.dto.response.PredefinedExerciseResponse;
import com.phillipe.NutriFit.model.ExerciseCategory;
import com.phillipe.NutriFit.config.RateLimitConfig;
import com.phillipe.NutriFit.service.JwtService;
import com.phillipe.NutriFit.service.UserService;
import com.phillipe.NutriFit.service.WorkoutPlanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExerciseController.class)
class ExerciseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkoutPlanService workoutPlanService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RateLimitConfig rateLimitConfig;

    @Test
    @WithMockUser
    void getPredefinedExercises_success_shouldReturnList() throws Exception {
        List<PredefinedExerciseResponse> exercises = Arrays.asList(
                PredefinedExerciseResponse.builder()
                        .id("1")
                        .name("Bench Press")
                        .category(ExerciseCategory.CHEST)
                        .build(),
                PredefinedExerciseResponse.builder()
                        .id("2")
                        .name("Squat")
                        .category(ExerciseCategory.QUADS)
                        .build()
        );

        when(workoutPlanService.getPredefinedExercises(null)).thenReturn(exercises);

        mockMvc.perform(get("/exercises/predefined"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Bench Press"))
                .andExpect(jsonPath("$[0].category").value("CHEST"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Squat"))
                .andExpect(jsonPath("$[1].category").value("QUADS"));

        verify(workoutPlanService).getPredefinedExercises(null);
    }

    @Test
    @WithMockUser
    void getPredefinedExercises_withCategoryFilter_shouldReturnFiltered() throws Exception {
        List<PredefinedExerciseResponse> chestExercises = Arrays.asList(
                PredefinedExerciseResponse.builder()
                        .id("1")
                        .name("Bench Press")
                        .category(ExerciseCategory.CHEST)
                        .build(),
                PredefinedExerciseResponse.builder()
                        .id("3")
                        .name("Incline Press")
                        .category(ExerciseCategory.CHEST)
                        .build()
        );

        when(workoutPlanService.getPredefinedExercises(ExerciseCategory.CHEST)).thenReturn(chestExercises);

        mockMvc.perform(get("/exercises/predefined")
                        .param("category", "CHEST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].category").value("CHEST"))
                .andExpect(jsonPath("$[1].category").value("CHEST"));

        verify(workoutPlanService).getPredefinedExercises(ExerciseCategory.CHEST);
    }

    @Test
    @WithMockUser
    void getCategories_success_shouldReturnAllCategories() throws Exception {
        List<ExerciseCategory> categories = Arrays.asList(ExerciseCategory.values());

        when(workoutPlanService.getCategories()).thenReturn(categories);

        mockMvc.perform(get("/exercises/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(ExerciseCategory.values().length))
                .andExpect(jsonPath("$[0]").value("BACK"))
                .andExpect(jsonPath("$[1]").value("CHEST"));

        verify(workoutPlanService).getCategories();
    }
}
