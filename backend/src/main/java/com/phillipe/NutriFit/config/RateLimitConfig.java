package com.phillipe.NutriFit.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration for API rate limiting using Bucket4j.
 * Provides different rate limit buckets for different endpoint types.
 */
@Component
public class RateLimitConfig {

    // Login: 5 attempts per minute per IP
    private static final int LOGIN_REQUESTS_PER_MINUTE = 5;

    // Register: 3 attempts per minute per IP
    private static final int REGISTER_REQUESTS_PER_MINUTE = 3;

    // General API: 100 requests per minute per user/IP
    private static final int GENERAL_REQUESTS_PER_MINUTE = 100;

    // Cache for login rate limit buckets (keyed by IP)
    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();

    // Cache for register rate limit buckets (keyed by IP)
    private final Map<String, Bucket> registerBuckets = new ConcurrentHashMap<>();

    // Cache for general API rate limit buckets (keyed by user ID or IP)
    private final Map<String, Bucket> generalBuckets = new ConcurrentHashMap<>();

    /**
     * Get or create a rate limit bucket for login attempts by IP.
     */
    public Bucket resolveLoginBucket(String clientIp) {
        return loginBuckets.computeIfAbsent(clientIp, this::createLoginBucket);
    }

    /**
     * Get or create a rate limit bucket for registration attempts by IP.
     */
    public Bucket resolveRegisterBucket(String clientIp) {
        return registerBuckets.computeIfAbsent(clientIp, this::createRegisterBucket);
    }

    /**
     * Get or create a rate limit bucket for general API requests.
     * Uses user ID if authenticated, otherwise falls back to client IP.
     */
    public Bucket resolveGeneralBucket(String key) {
        return generalBuckets.computeIfAbsent(key, this::createGeneralBucket);
    }

    private Bucket createLoginBucket(String key) {
        Bandwidth limit = Bandwidth.classic(
                LOGIN_REQUESTS_PER_MINUTE,
                Refill.intervally(LOGIN_REQUESTS_PER_MINUTE, Duration.ofMinutes(1))
        );
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createRegisterBucket(String key) {
        Bandwidth limit = Bandwidth.classic(
                REGISTER_REQUESTS_PER_MINUTE,
                Refill.intervally(REGISTER_REQUESTS_PER_MINUTE, Duration.ofMinutes(1))
        );
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createGeneralBucket(String key) {
        Bandwidth limit = Bandwidth.classic(
                GENERAL_REQUESTS_PER_MINUTE,
                Refill.intervally(GENERAL_REQUESTS_PER_MINUTE, Duration.ofMinutes(1))
        );
        return Bucket.builder().addLimit(limit).build();
    }

    /**
     * Get the number of requests allowed per minute for login.
     */
    public int getLoginRequestsPerMinute() {
        return LOGIN_REQUESTS_PER_MINUTE;
    }

    /**
     * Get the number of requests allowed per minute for registration.
     */
    public int getRegisterRequestsPerMinute() {
        return REGISTER_REQUESTS_PER_MINUTE;
    }

    /**
     * Get the number of requests allowed per minute for general API calls.
     */
    public int getGeneralRequestsPerMinute() {
        return GENERAL_REQUESTS_PER_MINUTE;
    }
}
