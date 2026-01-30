package com.phillipe.NutriFit.service;

import com.phillipe.NutriFit.repository.UserRepository;
import com.phillipe.NutriFit.model.entity.User;
import com.phillipe.NutriFit.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private MyUserDetailsService service;

    @Test
    void loadUserByUsername_shouldReturnUserPrincipalWhenUserExists() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("hashedPassword");

        when(repo.findByUsername("testuser")).thenReturn(user);

        // act
        UserDetails userDetails = service.loadUserByUsername("testuser");

        // assert
        assertNotNull(userDetails);
        assertInstanceOf(UserPrincipal.class, userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("hashedPassword", userDetails.getPassword());

        verify(repo).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_shouldThrowWhenUserNotFound() {
        // arrange
        when(repo.findByUsername("unknownuser")).thenReturn(null);

        // act & assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("unknownuser")
        );

        assertTrue(exception.getMessage().contains("unknownuser"));
        verify(repo).findByUsername("unknownuser");
    }

    @Test
    void loadUserByUsername_shouldReturnCorrectAuthority() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("hashedPassword");

        when(repo.findByUsername("testuser")).thenReturn(user);

        // act
        UserDetails userDetails = service.loadUserByUsername("testuser");

        // assert
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertEquals(1, authorities.size());

        GrantedAuthority authority = authorities.iterator().next();
        assertEquals("ROLE_USER", authority.getAuthority());
    }

    @Test
    void loadUserByUsername_shouldReturnEnabledAccount() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("hashedPassword");

        when(repo.findByUsername("testuser")).thenReturn(user);

        // act
        UserDetails userDetails = service.loadUserByUsername("testuser");

        // assert
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
    }
}
