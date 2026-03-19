package com.ecommerce.user.service;

import com.ecommerce.user.dto.AuthResponse;
import com.ecommerce.user.dto.LoginRequest;
import com.ecommerce.user.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthService authService;

    public AuthResponse register(RegisterRequest request) {
        return authService.register(request);
    }

    public AuthResponse login(LoginRequest request) {
        return authService.login(request);
    }
}

