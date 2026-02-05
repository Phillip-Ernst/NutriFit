package com.phillipe.NutriFit.config;

import com.phillipe.NutriFit.dto.response.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationException_shouldReturnBadRequestWithFieldErrors() {
        // arrange
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("object", "username", "must not be blank");
        FieldError fieldError2 = new FieldError("object", "password", "must be at least 6 characters");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        // act
        ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex);

        // assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().getError());
        assertEquals(2, response.getBody().getFieldErrors().size());
        assertEquals("username", response.getBody().getFieldErrors().get(0).getField());
        assertEquals("must not be blank", response.getBody().getFieldErrors().get(0).getMessage());
    }

    @Test
    void handleUsernameNotFound_shouldReturnNotFound() {
        // arrange
        UsernameNotFoundException ex = new UsernameNotFoundException("Username testuser not found");

        // act
        ResponseEntity<ErrorResponse> response = handler.handleUsernameNotFound(ex);

        // assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("USER_NOT_FOUND", response.getBody().getError());
        assertEquals("Username testuser not found", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleIllegalArgument_shouldReturnBadRequest() {
        // arrange
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input provided");

        // act
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex);

        // assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("BAD_REQUEST", response.getBody().getError());
        assertEquals("Invalid input provided", response.getBody().getMessage());
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        // arrange
        Exception ex = new RuntimeException("Something went wrong");

        // act
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        // assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_ERROR", response.getBody().getError());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getMessage());
        // Should not expose the actual error message
        assertFalse(response.getBody().getMessage().contains("Something went wrong"));
    }

    @Test
    void errorResponse_shouldHaveTimestamp() {
        // arrange
        UsernameNotFoundException ex = new UsernameNotFoundException("test");

        // act
        ResponseEntity<ErrorResponse> response = handler.handleUsernameNotFound(ex);

        // assert
        assertNotNull(response.getBody().getTimestamp());
    }
}
