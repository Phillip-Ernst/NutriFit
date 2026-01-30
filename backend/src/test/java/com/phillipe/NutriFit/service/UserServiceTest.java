package com.phillipe.NutriFit.service;

import com.phillipe.NutriFit.repository.UserRepository;
import com.phillipe.NutriFit.model.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void saveUser_shouldEncodePasswordBeforeSaving() {
        // arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("plainPassword");

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword123");
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            return savedUser;
        });

        // act
        User savedUser = userService.saveUser(user);

        // assert
        assertNotNull(savedUser);
        assertEquals("encodedPassword123", savedUser.getPassword());

        verify(passwordEncoder).encode("plainPassword");
        verify(userRepo).save(user);
    }

    @Test
    void saveUser_shouldPreserveUsernameUnchanged() {
        // arrange
        User user = new User();
        user.setUsername("originalUsername");
        user.setPassword("password");

        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        User savedUser = userService.saveUser(user);

        // assert
        assertEquals("originalUsername", savedUser.getUsername());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(captor.capture());
        assertEquals("originalUsername", captor.getValue().getUsername());
    }

    @Test
    void saveUser_shouldReturnSavedUserFromRepo() {
        // arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        User savedUserFromRepo = new User();
        savedUserFromRepo.setId(42L);
        savedUserFromRepo.setUsername("testuser");
        savedUserFromRepo.setPassword("encoded");

        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepo.save(any(User.class))).thenReturn(savedUserFromRepo);

        // act
        User result = userService.saveUser(user);

        // assert
        assertSame(savedUserFromRepo, result);
        assertEquals(42L, result.getId());
    }
}
