package com.phillipe.NutriFit.service;

import com.phillipe.NutriFit.repository.MealLogRepository;
import com.phillipe.NutriFit.dto.request.FoodItemRequest;
import com.phillipe.NutriFit.dto.request.MealLogRequest;
import com.phillipe.NutriFit.dto.response.MealLogResponse;
import com.phillipe.NutriFit.model.entity.MealLog;
import com.phillipe.NutriFit.repository.UserRepository;
import com.phillipe.NutriFit.model.entity.User;
import com.phillipe.NutriFit.service.impl.MealLogServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealLogServiceImplTest {

    @Mock
    private MealLogRepository mealLogRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private MealLogServiceImpl service;

    @Test
    void createMeal_shouldPersistMealForUser() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        FoodItemRequest food = FoodItemRequest.builder()
                .type("Chicken Breast")
                .calories(200)
                .protein(35)
                .carbs(0)
                .fats(5)
                .build();

        MealLogRequest request = MealLogRequest.builder()
                .foods(List.of(food))
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(mealLogRepo.save(any(MealLog.class))).thenAnswer(invocation -> {
            MealLog saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // act
        MealLogResponse response = service.createMeal(request, "testuser");

        // assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(200, response.getTotalCalories());
        assertEquals(35, response.getTotalProtein());
        assertEquals(0, response.getTotalCarbs());
        assertEquals(5, response.getTotalFats());
        assertEquals(1, response.getFoods().size());
        assertEquals("Chicken Breast", response.getFoods().get(0).getType());

        verify(userRepo).findByUsername("testuser");
        verify(mealLogRepo).save(any(MealLog.class));
    }

    @Test
    void createMeal_shouldThrowWhenUserNotFound() {
        // arrange
        MealLogRequest request = MealLogRequest.builder()
                .foods(List.of(FoodItemRequest.builder().type("Apple").build()))
                .build();

        when(userRepo.findByUsername("unknownuser")).thenReturn(null);

        // act & assert
        assertThrows(UsernameNotFoundException.class,
                () -> service.createMeal(request, "unknownuser"));

        verify(userRepo).findByUsername("unknownuser");
        verifyNoInteractions(mealLogRepo);
    }

    @Test
    void createMeal_shouldTreatNullNutritionValuesAsZero() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        FoodItemRequest food = FoodItemRequest.builder()
                .type("Unknown Food")
                .calories(null)
                .protein(null)
                .carbs(null)
                .fats(null)
                .build();

        MealLogRequest request = MealLogRequest.builder()
                .foods(List.of(food))
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(mealLogRepo.save(any(MealLog.class))).thenAnswer(invocation -> {
            MealLog saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // act
        MealLogResponse response = service.createMeal(request, "testuser");

        // assert
        assertEquals(0, response.getTotalCalories());
        assertEquals(0, response.getTotalProtein());
        assertEquals(0, response.getTotalCarbs());
        assertEquals(0, response.getTotalFats());
    }

    @Test
    void createMeal_shouldAggregateMultipleFoods() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        FoodItemRequest food1 = FoodItemRequest.builder()
                .type("Chicken Breast")
                .calories(200)
                .protein(35)
                .carbs(0)
                .fats(5)
                .build();

        FoodItemRequest food2 = FoodItemRequest.builder()
                .type("Rice")
                .calories(150)
                .protein(3)
                .carbs(30)
                .fats(1)
                .build();

        MealLogRequest request = MealLogRequest.builder()
                .foods(List.of(food1, food2))
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(mealLogRepo.save(any(MealLog.class))).thenAnswer(invocation -> {
            MealLog saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        // act
        MealLogResponse response = service.createMeal(request, "testuser");

        // assert
        assertEquals(350, response.getTotalCalories()); // 200 + 150
        assertEquals(38, response.getTotalProtein()); // 35 + 3
        assertEquals(30, response.getTotalCarbs()); // 0 + 30
        assertEquals(6, response.getTotalFats()); // 5 + 1
        assertEquals(2, response.getFoods().size());

        ArgumentCaptor<MealLog> captor = ArgumentCaptor.forClass(MealLog.class);
        verify(mealLogRepo).save(captor.capture());
        MealLog savedMeal = captor.getValue();
        assertEquals(user, savedMeal.getUser());
        assertEquals(2, savedMeal.getFoods().size());
    }

    @Test
    void getMyMeals_shouldReturnUserMeals() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        MealLog meal1 = MealLog.builder()
                .id(1L)
                .user(user)
                .createdAt(Instant.now())
                .totalCalories(500)
                .totalProtein(40)
                .totalCarbs(50)
                .totalFats(15)
                .build();

        MealLog meal2 = MealLog.builder()
                .id(2L)
                .user(user)
                .createdAt(Instant.now().minusSeconds(3600))
                .totalCalories(600)
                .totalProtein(45)
                .totalCarbs(60)
                .totalFats(20)
                .build();

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(mealLogRepo.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(meal1, meal2));

        // act
        List<MealLogResponse> responses = service.getMyMeals("testuser");

        // assert
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals(2L, responses.get(1).getId());

        verify(userRepo).findByUsername("testuser");
        verify(mealLogRepo).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getMyMeals_shouldReturnEmptyListWhenNoMeals() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepo.findByUsername("testuser")).thenReturn(user);
        when(mealLogRepo.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(Collections.emptyList());

        // act
        List<MealLogResponse> responses = service.getMyMeals("testuser");

        // assert
        assertTrue(responses.isEmpty());

        verify(userRepo).findByUsername("testuser");
        verify(mealLogRepo).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    void getMyMeals_shouldThrowNullPointerWhenUserNotFound() {
        // Note: This test documents the current behavior (bug) where
        // getMyMeals does not check if user is null before calling getId().
        // This will throw NullPointerException instead of UsernameNotFoundException.
        // arrange
        when(userRepo.findByUsername("unknownuser")).thenReturn(null);

        // act & assert
        assertThrows(NullPointerException.class,
                () -> service.getMyMeals("unknownuser"));

        verify(userRepo).findByUsername("unknownuser");
    }
}
