package com.phillipe.NutriFit.config;

import com.phillipe.NutriFit.config.filter.JwtFilter;
import com.phillipe.NutriFit.config.filter.RateLimitFilter;
import com.phillipe.NutriFit.config.oauth2.NutriFitOidcUserService;
import com.phillipe.NutriFit.config.oauth2.OAuth2AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final RateLimitFilter rateLimitFilter;
    private final NutriFitOidcUserService oidcUserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;

    @Value("${cors.allowed-origins:http://localhost:5173}")
    private String allowedOriginsConfig;

    public SecurityConfig(JwtFilter jwtFilter,
                          RateLimitFilter rateLimitFilter,
                          NutriFitOidcUserService oidcUserService,
                          OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler) {
        this.jwtFilter = jwtFilter;
        this.rateLimitFilter = rateLimitFilter;
        this.oidcUserService = oidcUserService;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                // IF_REQUIRED allows OAuth2 to store state in session during the auth flow;
                // JWT-authenticated API calls will not create sessions.
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/register", "/api/login",
                                "/register", "/login",
                                "/actuator/**",
                                // OAuth2 authorization initiation and callback
                                "/api/oauth2/**", "/api/login/oauth2/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        // Custom base URIs so paths align with the /api servlet prefix
                        .authorizationEndpoint(ae ->
                                ae.baseUri("/api/oauth2/authorization"))
                        .redirectionEndpoint(re ->
                                re.baseUri("/api/login/oauth2/code/*"))
                        .userInfoEndpoint(u -> u
                                .oidcUserService(oidcUserService)  // Google (OIDC)
                        )
                        .successHandler(oAuth2SuccessHandler)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")))
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        List<String> allowedOrigins = Arrays.stream(allowedOriginsConfig.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
