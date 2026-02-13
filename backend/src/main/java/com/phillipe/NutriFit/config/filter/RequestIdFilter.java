package com.phillipe.NutriFit.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter that generates or propagates request correlation IDs for debugging.
 * The request ID is:
 * - Taken from incoming X-Request-ID header if present
 * - Generated as a new UUID if not present
 * - Added to response headers as X-Request-ID
 * - Added to MDC for logging
 */
@Component
@Order(0) // Run first, before rate limiting and auth
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String MDC_REQUEST_ID_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestId = request.getHeader(REQUEST_ID_HEADER);

        if (requestId == null || requestId.isBlank()) {
            requestId = generateRequestId();
        }

        // Add to MDC for logging
        MDC.put(MDC_REQUEST_ID_KEY, requestId);

        // Add to response headers
        response.addHeader(REQUEST_ID_HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Clean up MDC
            MDC.remove(MDC_REQUEST_ID_KEY);
        }
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Apply to all requests including actuator for consistency
        return false;
    }
}
