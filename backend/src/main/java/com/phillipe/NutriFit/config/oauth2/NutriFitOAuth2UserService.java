package com.phillipe.NutriFit.config.oauth2;

import com.phillipe.NutriFit.model.entity.User;
import com.phillipe.NutriFit.service.UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles OAuth2 user info for non-OIDC providers (e.g., GitHub).
 * Finds or creates a local User, then returns an OAuth2User with the
 * local username set as the name attribute so the success handler can
 * generate a JWT without any additional lookups.
 */
@Service
public class NutriFitOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    public NutriFitOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String provider = registrationId.toUpperCase();

        String providerId = extractProviderId(registrationId, oAuth2User);
        String preferredUsername = extractPreferredUsername(registrationId, oAuth2User);

        User user = userService.findOrCreateOAuthUser(provider, providerId, preferredUsername);

        // Store the local username as the name attribute so the success handler
        // can call jwtService.generateToken(oAuth2User.getName()) directly.
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("_nutrifit_username", user.getUsername());

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "_nutrifit_username"
        );
    }

    /** Returns the unique user ID string from the provider's attributes. */
    static String extractProviderId(String registrationId, OAuth2User oAuth2User) {
        return switch (registrationId) {
            case "github" -> String.valueOf(oAuth2User.getAttribute("id"));
            default -> throw new OAuth2AuthenticationException(
                    "Unsupported OAuth2 provider: " + registrationId);
        };
    }

    /** Returns a human-friendly name to use as a starting point for the local username. */
    static String extractPreferredUsername(String registrationId, OAuth2User oAuth2User) {
        return switch (registrationId) {
            case "github" -> {
                String login = oAuth2User.getAttribute("login");
                yield login != null ? login : "github_user";
            }
            default -> "user";
        };
    }
}
