package com.phillipe.nutrifit.nutrition.config.filter;

import com.phillipe.nutrifit.nutrition.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtService.extractUserName(token);
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                log.debug("JWT token expired for request to {}", request.getRequestURI());
            } catch (io.jsonwebtoken.MalformedJwtException e) {
                log.warn("Malformed JWT token for request to {}", request.getRequestURI());
            } catch (io.jsonwebtoken.security.SignatureException e) {
                log.warn("Invalid JWT signature for request to {}", request.getRequestURI());
            } catch (Exception e) {
                log.warn("JWT validation failed for request to {}: {}", request.getRequestURI(), e.getClass().getSimpleName());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null
                && !jwtService.isTokenExpired(token)) {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}