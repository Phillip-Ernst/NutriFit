package com.phillipe.NutriFit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    // Test secret key (minimum 32 characters for HS256)
    private static final String TEST_SECRET = "test-jwt-secret-key-for-unit-tests-minimum-32-chars";
    private static final String DIFFERENT_SECRET = "different-secret-key-also-32-chars-minimum-length";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(TEST_SECRET);
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
    void constructor_shouldRejectNullSecret() {
        assertThrows(IllegalArgumentException.class, () -> new JwtService(null));
    }

    @Test
    void constructor_shouldRejectBlankSecret() {
        assertThrows(IllegalArgumentException.class, () -> new JwtService(""));
        assertThrows(IllegalArgumentException.class, () -> new JwtService("   "));
    }

    @Test
    void constructor_shouldRejectShortSecret() {
        // Less than 32 characters should be rejected
        assertThrows(IllegalArgumentException.class, () -> new JwtService("short-secret"));
    }

    @Test
    void sameSecret_shouldProduceInterchangeableTokens() {
        // Two services with the same secret should be able to validate each other's tokens
        JwtService service1 = new JwtService(TEST_SECRET);
        JwtService service2 = new JwtService(TEST_SECRET);

        String token = service1.generateToken("testuser");
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        // Token from service1 should be valid on service2 (same secret)
        assertTrue(service2.validateToken(token, userDetails));
    }

    @Test
    void differentSecrets_shouldNotBeInterchangeable() {
        // Two services with different secrets should NOT be able to validate each other's tokens
        JwtService service1 = new JwtService(TEST_SECRET);
        JwtService service2 = new JwtService(DIFFERENT_SECRET);

        String token = service1.generateToken("testuser");
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        // Token from service1 should fail validation on service2 (different secret)
        assertThrows(Exception.class, () -> service2.validateToken(token, userDetails));
    }
}
