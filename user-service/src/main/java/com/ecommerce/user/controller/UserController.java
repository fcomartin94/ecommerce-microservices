package com.ecommerce.user.controller;

import com.ecommerce.user.dto.*;
import com.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user authentication endpoints.
 *
 * <p>Base path: {@code /api/users}. All requests are routed here through the
 * API Gateway ({@code lb://user-service}).</p>
 *
 * <p>Successful responses include a JWT token in the {@link AuthResponse} body;
 * clients should store it and pass it as {@code Authorization: Bearer <token>}
 * on subsequent requests.</p>
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Registers a new user account and returns a JWT token.
     *
     * <p>{@code POST /api/users/register}</p>
     *
     * @param request validated registration payload (name, email, password)
     * @return {@code 200 OK} with an {@link AuthResponse} containing the JWT token,
     *         email, name, and role; {@code 400} if validation fails
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    /**
     * Authenticates an existing user and returns a JWT token.
     *
     * <p>{@code POST /api/users/login}</p>
     *
     * @param request validated login payload (email, password)
     * @return {@code 200 OK} with an {@link AuthResponse} containing the JWT token;
     *         {@code 401} if credentials are invalid
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
