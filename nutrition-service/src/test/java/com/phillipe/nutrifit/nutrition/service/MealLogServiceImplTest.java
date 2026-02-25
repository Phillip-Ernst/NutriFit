package com.phillipe.nutrifit.nutrition.service;

import com.phillipe.nutrifit.nutrition.dto.request.FoodItemRequest;
import com.phillipe.nutrifit.nutrition.dto.request.MealLogRequest;
import com.phillipe.nutrifit.nutrition.dto.response.MealLogResponse;
import com.phillipe.nutrifit.nutrition.model.entity.MealLog;
import com.phillipe.nutrifit.nutrition.repository.MealLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MealLogServiceImplTest {

    @Mock
    private MealLogRepository mealLogRepository;

    @InjectMocks
    private MealLogServiceImpl service;

    @Test
    void createMeal_shouldPersistMealForUser() {
        MealLogRequest request = MealLogRequest.builder()
                .foods(List.of(FoodItemRequest.builder()
                        .type("Chicken")
                        .calories(300)
                        .protein(40)
                        .carbs(0)
                        .fats(10)
                        .build()))
                .build();

        when(mealLogRepository.save(any(MealLog.class))).thenAnswer(inv -> {
            MealLog m = inv.getArgument(0);
            m.setId(1L);
            return m;
        });

        MealLogResponse response = service.createMeal(request, "testuser");

        ArgumentCaptor<MealLog> captor = ArgumentCaptor.forClass(MealLog.class);
        verify(mealLogRepository).save(captor.capture());

        MealLog saved = captor.getValue();
        assertEquals("testuser", saved.getUsername());
        assertEquals(300, saved.getTotalCalories());
        assertEquals(40, saved.getTotalProtein());
        assertEquals(0, saved.getTotalCarbs());
        assertEquals(10, saved.getTotalFats());
        assertEquals(1, saved.getFoods().size());

        assertEquals(1L, response.getId());
        assertEquals(300, response.getTotalCalories());
    }

    @Test
    void createMeal_shouldTreatNullNutritionValuesAsZero() {
        MealLogRequest request = MealLogRequest.builder()
                .foods(List.of(FoodItemRequest.builder()
                        .type("Unknown food")
                        .calories(null)
                        .protein(null)
                        .carbs(null)
                        .fats(null)
                        .build()))
                .build();

        when(mealLogRepository.save(any(MealLog.class))).thenAnswer(inv -> {
            MealLog m = inv.getArgument(0);
            m.setId(1L);
            return m;
        });

        MealLogResponse response = service.createMeal(request, "testuser");

        assertEquals(0, response.getTotalCalories());
        assertEquals(0, response.getTotalProtein());
        assertEquals(0, response.getTotalCarbs());
        assertEquals(0, response.getTotalFats());
    }

    @Test
    void createMeal_shouldAggregateMultipleFoods() {
        MealLogRequest request = MealLogRequest.builder()
                .foods(List.of(
                        FoodItemRequest.builder().type("Rice").calories(200).protein(5).carbs(40).fats(1).build(),
                        FoodItemRequest.builder().type("Chicken").calories(300).protein(40).carbs(0).fats(10).build()
                ))
                .build();

        when(mealLogRepository.save(any(MealLog.class))).thenAnswer(inv -> {
            MealLog m = inv.getArgument(0);
            m.setId(1L);
            return m;
        });

        MealLogResponse response = service.createMeal(request, "testuser");

        assertEquals(500, response.getTotalCalories());
        assertEquals(45, response.getTotalProtein());
        assertEquals(40, response.getTotalCarbs());
        assertEquals(11, response.getTotalFats());
    }

    @Test
    void getMyMeals_shouldReturnUserMeals() {
        MealLog meal = MealLog.builder()
                .username("testuser")
                .createdAt(Instant.now())
                .totalCalories(500)
                .totalProtein(45)
                .totalCarbs(40)
                .totalFats(11)
                .build();
        meal.setId(1L);

        when(mealLogRepository.findByUsernameOrderByCreatedAtDesc("testuser")).thenReturn(List.of(meal));

        List<MealLogResponse> result = service.getMyMeals("testuser");

        assertEquals(1, result.size());
        assertEquals(500, result.get(0).getTotalCalories());
    }

    @Test
    void getMyMeals_shouldReturnEmptyListWhenNoMeals() {
        when(mealLogRepository.findByUsernameOrderByCreatedAtDesc("newuser")).thenReturn(List.of());

        List<MealLogResponse> result = service.getMyMeals("newuser");

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteMeal_shouldDeleteMealOwnedByUser() {
        MealLog meal = MealLog.builder().username("testuser").build();
        meal.setId(1L);

        when(mealLogRepository.findByIdAndUsername(1L, "testuser")).thenReturn(Optional.of(meal));

        service.deleteMeal(1L, "testuser");

        verify(mealLogRepository).delete(meal);
    }

    @Test
    void deleteMeal_shouldThrowWhenMealNotFoundOrAccessDenied() {
        when(mealLogRepository.findByIdAndUsername(99L, "testuser")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.deleteMeal(99L, "testuser"));
    }
}