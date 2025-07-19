package com.daisy.tickets.filters;


import com.daisy.tickets.domain.entities.User;
import com.daisy.tickets.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserProvisioningFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null &&
                auth.isAuthenticated() &&
                auth.getPrincipal() instanceof Jwt jwt) {
            UUID keycloakId = UUID.fromString(jwt.getSubject());

            if (!userRepository.existsById(keycloakId)) {

                // Extract the info from Keycloak claims to create new User
                User newUser = new User();
                newUser.setId(keycloakId);
                newUser.setName(jwt.getClaims().get("preferred_name").toString());
                newUser.setEmail(jwt.getClaims().get("email").toString());

                userRepository.save(newUser);
            }

            filterChain.doFilter(request, response);
        }
    }
}
