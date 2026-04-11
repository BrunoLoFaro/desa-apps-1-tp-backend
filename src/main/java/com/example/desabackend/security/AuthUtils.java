package com.example.desabackend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility to get the authenticated user from the SecurityContext.
 */
public final class AuthUtils {

    private AuthUtils() {
    }

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new IllegalStateException("No authenticated user found in security context");
        }
        return principal.userId();
    }
}
