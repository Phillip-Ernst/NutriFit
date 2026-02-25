package com.phillipe.nutrifit.nutrition.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phillipe.nutrifit.nutrition.config.SecurityConfig;
import com.phillipe.nutrifit.nutrition.dto.request.FoodItemRequest;
import com.phillipe.nutrifit.nutrition.dto.request.MealLogRequest;
import com.phillipe.nutrifit.nutrition.dto.response.FoodItemResponse;
import com.phillipe.nutrifit.nutrition.dto.response.MealLogResponse;
import com.phillipe.nutrifit.nutrition.service.JwtService;
import com.phillipe.nutrifit.nutrition.service.MealLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MealLogController.class)
@Import(SecurityConfig.class)
class MealLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MealLogService mealLogService;

    @MockitoBean
    private JwtService jwtService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createMeal_success_shouldReturnMealLogResponse() throws Exception {
        MealLogRequest request = MealLogRequest.builder()
                .foods(List.of(FoodItemRequest.builder()
                        .type("Chicken")
                        .calories(300)
                        .protein(40)
                        .carbs(0)
                        .fats(10)
                        .build()))
                .build();

        MealLogResponse response = MealLogResponse.builder()
                .id(1L)
                .createdAt(Instant.now())
                .totalCalories(300)
                .totalProtein(40)
                .totalCarbs(0)
                .totalFats(10)
                .foods(List.of(FoodItemResponse.builder().type("Chicken").calories(300).protein(40).carbs(0).fats(10).build()))
                .build();

        when(mealLogService.createMeal(any(), eq("testuser"))).thenReturn(response);

        mockMvc.perform(post("/meals")
                        .with(csrf())
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.totalCalories").value(300))
                .andExpect(jsonPath("$.totalProtein").value(40));
    }

    @Test
    void createMeal_emptyFoodsList_shouldReturnValidationError() throws Exception {
        MealLogRequest request = MealLogRequest.builder().foods(List.of()).build();

        mockMvc.perform(post("/meals")
                        .with(csrf())
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMeal_foodWithoutType_shouldReturnValidationError() throws Exception {
        MealLogRequest request = MealLogRequest.builder()
                .foods(List.of(FoodItemRequest.builder().type("").calories(100).build()))
                .build();

        mockMvc.perform(post("/meals")
                        .with(csrf())
                        .with(user("testuser"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createMeal_unauthenticated_shouldReturnForbidden() throws Exception {
        MealLogRequest request = MealLogRequest.builder()
                .foods(List.of(FoodItemRequest.builder().type("Chicken").calories(300).build()))
                .build();

        mockMvc.perform(post("/meals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMyMeals_success_shouldReturnMealsList() throws Exception {
        MealLogResponse meal = MealLogResponse.builder()
                .id(1L)
                .createdAt(Instant.now())
                .totalCalories(500)
                .foods(List.of())
                .build();

        when(mealLogService.getMyMeals("testuser")).thenReturn(List.of(meal));

        mockMvc.perform(get("/meals/mine")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].totalCalories").value(500));
    }

    @Test
    void getMyMeals_emptyList_shouldReturnEmptyArray() throws Exception {
        when(mealLogService.getMyMeals("testuser")).thenReturn(List.of());

        mockMvc.perform(get("/meals/mine")
                        .with(user("testuser")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getMyMeals_unauthenticated_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/meals/mine"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteMeal_success_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/meals/1")
                        .with(csrf())
                        .with(user("testuser")))
                .andExpect(status().isNoContent());

        verify(mealLogService).deleteMeal(1L, "testuser");
    }

    @Test
    void deleteMeal_unauthenticated_shouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/meals/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}