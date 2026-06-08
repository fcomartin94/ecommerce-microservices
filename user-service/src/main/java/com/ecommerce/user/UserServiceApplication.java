package com.ecommerce.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the User Service.
 *
 * <p>Handles user registration and authentication on port {@code 8081},
 * backed by an H2 in-memory database. Issues signed JWT tokens (HS256)
 * on successful register/login; all subsequent requests must carry the
 * token in the {@code Authorization: Bearer <token>} header.</p>
 *
 * @see com.ecommerce.user.service.AuthService
 * @see com.ecommerce.user.security.JwtService
 */
@SpringBootApplication
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}