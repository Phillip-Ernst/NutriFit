package com.phillipe.NutriFit.config.filter;

import com.phillipe.NutriFit.config.RateLimitConfig;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that enforces rate limiting on API endpoints.
 * Different limits apply to different endpoint types:
 * - /api/login: 5 requests per minute per IP
 * - /api/register: 3 requests per minute per IP
 * - Other API endpoints: 100 requests per minute per user
 */
@Component
@Order(1) // Run before other filters
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    private final RateLimitConfig rateLimitConfig;

    public RateLimitFilter(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Don't rate limit actuator endpoints or non-API paths
        return path.startsWith("/actuator") || !path.startsWith("/api");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String clientIp = getClientIp(request);

        Bucket bucket;
        String limitType;

        if (path.equals("/api/login") || path.equals("/login")) {
            bucket = rateLimitConfig.resolveLoginBucket(clientIp);
            limitType = "login";
        } else if (path.equals("/api/register") || path.equals("/register")) {
            bucket = rateLimitConfig.resolveRegisterBucket(clientIp);
            limitType = "register";
        } else {
            // For authenticated endpoints, use user ID if available
            String key = getAuthenticatedUserKey(clientIp);
            bucket = rateLimitConfig.resolveGeneralBucket(key);
            limitType = "general";
        }

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // Add rate limit headers
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            // Rate limit exceeded
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;

            log.warn("Rate limit exceeded for {} endpoint. IP: {}, retry after: {}s",
                    limitType, maskIp(clientIp), waitForRefill);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));

            String errorJson = String.format(
                    "{\"error\":\"RATE_LIMIT_EXCEEDED\",\"message\":\"Too many requests. Please try again in %d seconds.\",\"retryAfterSeconds\":%d}",
                    waitForRefill, waitForRefill
            );
            response.getWriter().write(errorJson);
        }
    }

    /**
     * Get the client IP address, accounting for proxies.
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // Take the first IP in the chain (original client)
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * Get a key for rate limiting based on authenticated user or IP.
     */
    private String getAuthenticatedUserKey(String clientIp) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "user:" + auth.getName();
        }
        return "ip:" + clientIp;
    }

    /**
     * Mask IP address for logging (privacy).
     */
    private String maskIp(String ip) {
        if (ip == null) return "unknown";
        if (ip.contains(".")) {
            // IPv4: mask last octet
            int lastDot = ip.lastIndexOf('.');
            return ip.substring(0, lastDot) + ".xxx";
        } else if (ip.contains(":")) {
            // IPv6: mask last half
            return ip.substring(0, Math.min(ip.length(), 10)) + "::xxxx";
        }
        return "masked";
    }
}
