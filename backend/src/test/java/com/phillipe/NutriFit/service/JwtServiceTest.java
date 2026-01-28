package com.phillipe.NutriFit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    void generateToken_shouldReturnValidJwtFormat() {
        // act
        String token = jwtService.generateToken("testuser");

        // assert
        assertNotNull(token);
        // JWT tokens have 3 parts separated by dots
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have 3 parts (header.payload.signature)");
    }

    @Test
    void extractUserName_shouldReturnCorrectUsername() {
        // arrange
        String token = jwtService.generateToken("testuser");

        // act
        String extractedUsername = jwtService.extractUserName(token);

        // assert
        assertEquals("testuser", extractedUsername);
    }

    @Test
    void extractUserName_shouldWorkWithDifferentUsernames() {
        // arrange & act & assert
        String token1 = jwtService.generateToken("user1");
        assertEquals("user1", jwtService.extractUserName(token1));

        String token2 = jwtService.generateToken("admin@example.com");
        assertEquals("admin@example.com", jwtService.extractUserName(token2));

        String token3 = jwtService.generateToken("user-with-dashes");
        assertEquals("user-with-dashes", jwtService.extractUserName(token3));
    }

    @Test
    void validateToken_shouldReturnTrueForValidTokenAndMatchingUser() {
        // arrange
        String token = jwtService.generateToken("testuser");
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        // act
        boolean isValid = jwtService.validateToken(token, userDetails);

        // assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_shouldReturnFalseForMismatchedUsername() {
        // arrange
        String token = jwtService.generateToken("testuser");
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("differentuser");

        // act
        boolean isValid = jwtService.validateToken(token, userDetails);

        // assert
        assertFalse(isValid);
    }

    @Test
    void tokenLifecycle_generateExtractValidate() {
        // This test verifies the complete token lifecycle
        // arrange
        String username = "lifecycle-test-user";
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);

        // act - generate
        String token = jwtService.generateToken(username);
        assertNotNull(token);

        // act - extract
        String extractedUsername = jwtService.extractUserName(token);
        assertEquals(username, extractedUsername);

        // act - validate
        boolean isValid = jwtService.validateToken(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    void generateSecretKey_shouldReturnBase64EncodedKey() {
        // act
        String secretKey = jwtService.generateSecretKey();

        // assert
        assertNotNull(secretKey);
        assertFalse(secretKey.isEmpty());
        // Should be valid Base64
        assertDoesNotThrow(() -> java.util.Base64.getDecoder().decode(secretKey));
    }

    @Test
    void differentJwtServiceInstances_shouldGenerateDifferentTokens() {
        // This documents the behavior that each JwtService instance
        // generates its own secret key, so tokens are not interchangeable
        // arrange
        JwtService service1 = new JwtService();
        JwtService service2 = new JwtService();

        // act
        String token1 = service1.generateToken("testuser");
        String token2 = service2.generateToken("testuser");

        // assert - tokens should be different due to different secret keys
        assertNotEquals(token1, token2);

        // token from service1 should only be valid with service1
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        assertTrue(service1.validateToken(token1, userDetails));
        // token1 would fail validation on service2 (different key)
        assertThrows(Exception.class, () -> service2.validateToken(token1, userDetails));
    }
}
