package com.phillipe.NutriFit.config.oauth2;

import com.phillipe.NutriFit.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Called by Spring Security after a successful OAuth2 login.
 * Generates a JWT for the authenticated user and redirects the browser
 * to the frontend callback page with the token as a query parameter.
 */
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final String frontendUrl;

    public OAuth2AuthenticationSuccessHandler(
            JwtService jwtService,
            @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl) {
        this.jwtService = jwtService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // getName() returns the local NutriFit username stored during loadUser()
        String username = oAuth2User.getName();
        String token = jwtService.generateToken(username);
        response.sendRedirect(frontendUrl + "/oauth2/callback?token=" + token);
    }
}
