package com.phillipe.NutriFit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phillipe.NutriFit.dto.request.FoodItemRequest;
import com.phillipe.NutriFit.dto.request.MealLogRequest;
import com.phillipe.NutriFit.dto.response.MealLogResponse;
import com.phillipe.NutriFit.service.JwtService;
import com.phillipe.NutriFit.service.MealLogService;
import com.phillipe.NutriFit.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MealLogController.class)
class MealLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private MealLogService mealLogService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    // ==================== CREATE MEAL TESTS ====================

    @Test
    @WithMockUser(username = "testuser")
    void createMeal_success_shouldReturnMealLogResponse() throws Exception {
        FoodItemRequest foodItem = FoodItemRequest.builder()
                .type("Chicken Breast")
                .calories(165)
                .protein(31)
                .carbs(0)
                .fats(4)
                .build();

        MealLogRequest request = MealLogRequest.builder()
                .foods(List.of(foodItem))
                .build();

        MealLogResponse response = MealLogResponse.builder()
                .id(1L)
                .createdAt(Instant.now())
                .totalCalories(165)
                .totalProtein(31)
                .totalCarbs(0)
                .totalFats(4)
                .foods(List.of(foodItem))
                .build();

        when(mealLogService.createMeal(any(MealLogRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/meals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.totalCalories").value(165))
                .andExpect(jsonPath("$.totalProtein").value(31))
                .andExpect(jsonPath("$.foods[0].type").value("Chicken Breast"));

        verify(mealLogService).createMeal(any(MealLogRequest.class), eq("testuser"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createMeal_multipleFoods_shouldReturnAggregatedTotals() throws Exception {
        FoodItemRequest food1 = FoodItemRequest.builder()
                .type("Rice")
                .calories(200)
                .protein(4)
                .carbs(45)
                .fats(1)
                .build();

        FoodItemRequest food2 = FoodItemRequest.builder()
                .type("Beans")
                .calories(150)
                .protein(9)
                .carbs(27)
                .fats(1)
                .build();

        MealLogRequest request = MealLogRequest.builder()
                .foods(List.of(food1, food2))
                .build();

        MealLogResponse response = MealLogResponse.builder()
                .id(2L)
                .createdAt(Instant.now())
                .totalCalories(350)
                .totalProtein(13)
                .totalCarbs(72)
                .totalFats(2)
                .foods(List.of(food1, food2))
                .build();

        when(mealLogService.createMeal(any(MealLogRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/meals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalories").value(350))
                .andExpect(jsonPath("$.foods.length()").value(2));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createMeal_emptyFoodsList_shouldReturnValidationError() throws Exception {
        MealLogRequest request = MealLogRequest.builder()
                .foods(Collections.emptyList())
                .build();

        mockMvc.perform(post("/meals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(mealLogService, never()).createMeal(any(), any());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createMeal_nullFoodsList_shouldReturnValidationError() throws Exception {
        MealLogRequest request = MealLogRequest.builder()
                .foods(null)
                .build();

        mockMvc.perform(post("/meals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(mealLogService, never()).createMeal(any(), any());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createMeal_foodWithoutType_shouldReturnValidationError() throws Exception {
        FoodItemRequest foodItem = FoodItemRequest.builder()
                .type("")  // blank type should fail validation
                .calories(100)
                .build();

        MealLogRequest request = MealLogRequest.builder()
                .foods(List.of(foodItem))
                .build();

        mockMvc.perform(post("/meals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(mealLogService, never()).createMeal(any(), any());
    }

    @Test
    void createMeal_unauthenticated_shouldReturnUnauthorized() throws Exception {
        FoodItemRequest foodItem = FoodItemRequest.builder()
                .type("Apple")
                .calories(95)
                .build();

        MealLogRequest request = MealLogRequest.builder()
                .foods(List.of(foodItem))
                .build();

        mockMvc.perform(post("/meals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser")
    void createMeal_foodWithNullNutrients_shouldSucceed() throws Exception {
        FoodItemRequest foodItem = FoodItemRequest.builder()
                .type("Unknown Food")
                .calories(null)
                .protein(null)
                .carbs(null)
                .fats(null)
                .build();

        MealLogRequest request = MealLogRequest.builder()
                .foods(List.of(foodItem))
                .build();

        MealLogResponse response = MealLogResponse.builder()
                .id(3L)
                .createdAt(Instant.now())
                .totalCalories(0)
                .totalProtein(0)
                .totalCarbs(0)
                .totalFats(0)
                .foods(List.of(foodItem))
                .build();

        when(mealLogService.createMeal(any(MealLogRequest.class), eq("testuser")))
                .thenReturn(response);

        mockMvc.perform(post("/meals")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L));
    }

    // ==================== GET MY MEALS TESTS ====================

    @Test
    @WithMockUser(username = "testuser")
    void getMyMeals_success_shouldReturnMealsList() throws Exception {
        FoodItemRequest foodItem = FoodItemRequest.builder()
                .type("Oatmeal")
                .calories(150)
                .protein(5)
                .carbs(27)
                .fats(3)
                .build();

        MealLogResponse meal1 = MealLogResponse.builder()
                .id(1L)
                .createdAt(Instant.now())
                .totalCalories(150)
                .totalProtein(5)
                .totalCarbs(27)
                .totalFats(3)
                .foods(List.of(foodItem))
                .build();

        MealLogResponse meal2 = MealLogResponse.builder()
                .id(2L)
                .createdAt(Instant.now())
                .totalCalories(300)
                .totalProtein(25)
                .totalCarbs(35)
                .totalFats(10)
                .foods(List.of(foodItem))
                .build();

        when(mealLogService.getMyMeals("testuser")).thenReturn(List.of(meal1, meal2));

        mockMvc.perform(get("/meals/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(mealLogService).getMyMeals("testuser");
    }

    @Test
    @WithMockUser(username = "newuser")
    void getMyMeals_emptyList_shouldReturnEmptyArray() throws Exception {
        when(mealLogService.getMyMeals("newuser")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/meals/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(mealLogService).getMyMeals("newuser");
    }

    @Test
    void getMyMeals_unauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/meals/mine"))
                .andExpect(status().isUnauthorized());

        verify(mealLogService, never()).getMyMeals(any());
    }

    @Test
    @WithMockUser(username = "user1")
    void getMyMeals_differentUser_shouldOnlyGetOwnMeals() throws Exception {
        // This test verifies that the username from authentication is used
        when(mealLogService.getMyMeals("user1")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/meals/mine"))
                .andExpect(status().isOk());

        // Verify that the service was called with the authenticated user's name
        verify(mealLogService).getMyMeals("user1");
        verify(mealLogService, never()).getMyMeals("user2");
    }
}
