package com.ecommerce.user.service;

import com.ecommerce.user.dto.AuthResponse;
import com.ecommerce.user.dto.LoginRequest;
import com.ecommerce.user.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Application-level facade for user authentication operations.
 *
 * <p>Thin delegation layer between {@link com.ecommerce.user.controller.UserController}
 * and {@link AuthService}. Keeping the controller decoupled from
 * {@code AuthService} directly makes it easier to introduce additional
 * business logic (e.g. audit logging, rate limiting) here without touching
 * the controller or the core auth logic.</p>
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthService authService;

    /**
     * Registers a new user account and returns a JWT token.
     *
     * @param request the registration payload (name, email, password)
     * @return an {@link AuthResponse} containing the JWT token and user info
     * @throws RuntimeException if the email is already registered
     */
    public AuthResponse register(RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request the login payload (email, password)
     * @return an {@link AuthResponse} containing the JWT token and user info
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are invalid
     */
    public AuthResponse login(LoginRequest request) {
        return authService.login(request);
    }
}

