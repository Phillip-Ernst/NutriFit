package com.phillipe.NutriFit.config.oauth2;

import com.phillipe.NutriFit.model.entity.User;
import com.phillipe.NutriFit.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles OIDC user info for providers that use OpenID Connect (e.g., Google).
 * Delegates token verification to the standard OidcUserService, then finds or
 * creates a local User and returns an OidcUser with the local username as the
 * name attribute.
 */
@Service
public class NutriFitOidcUserService extends OidcUserService {

    private final UserService userService;

    public NutriFitOidcUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String provider = registrationId.toUpperCase();

        // Google's subject claim is the stable unique user ID
        String providerId = oidcUser.getSubject();

        String preferredUsername = extractPreferredUsername(oidcUser);

        User user = userService.findOrCreateOAuthUser(provider, providerId, preferredUsername);

        // "sub" is always present in OIDC responses and avoids a MissingAttributeException.
        // Override getName() so the success handler can call oAuth2User.getName() to get
        // the local NutriFit username directly.
        return new DefaultOidcUser(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                "sub"
        ) {
            @Override
            public String getName() {
                return user.getUsername();
            }
        };
    }

    private String extractPreferredUsername(OidcUser oidcUser) {
        // Prefer email prefix (e.g., "john.doe" from "john.doe@gmail.com")
        String email = oidcUser.getEmail();
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf('@'));
        }
        String name = oidcUser.getFullName();
        return name != null ? name : "google_user";
    }
}
