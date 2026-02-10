package com.phillipe.NutriFit.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class WebConfig {

    @Value("${cors.allowed-origins:http://localhost:5173}")
    private String allowedOriginsConfig;

    @Bean
    public FilterRegistrationBean<Filter> corsFilterRegistration() {
        // Parse allowed origins from config (comma-separated)
        Set<String> allowedOrigins = Arrays.stream(allowedOriginsConfig.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                HttpServletRequest httpRequest = (HttpServletRequest) request;

                String requestOrigin = httpRequest.getHeader("Origin");

                // Only set CORS headers if origin is in the allowlist
                if (requestOrigin != null && allowedOrigins.contains(requestOrigin)) {
                    httpResponse.setHeader("Access-Control-Allow-Origin", requestOrigin);
                    httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                    httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
                    httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
                    httpResponse.setHeader("Access-Control-Max-Age", "3600");
                }

                if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
                    // Only return OK for preflight if origin is allowed
                    if (requestOrigin != null && allowedOrigins.contains(requestOrigin)) {
                        httpResponse.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    }
                    return;
                }

                chain.doFilter(request, response);
            }
        });
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
