package com.ecommerce.user.service;

import com.ecommerce.user.dto.AuthResponse;
import com.ecommerce.user.dto.LoginRequest;
import com.ecommerce.user.dto.RegisterRequest;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService, authenticationManager);
    }

    @Test
    void register_newUser_returnsTokenAndUserInfo() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Alice");
        request.setEmail("alice@test.com");
        request.setPassword("secret");

        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("alice@test.com");
        assertThat(response.getName()).isEqualTo("Alice");
    }

    @Test
    void register_duplicateEmail_throwsRuntimeException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("alice@test.com");
        request.setPassword("secret");
        request.setName("Alice");

        when(userRepository.existsByEmail("alice@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("email");
    }

    @Test
    void login_validCredentials_returnsToken() {
        LoginRequest request = new LoginRequest();
        request.setEmail("alice@test.com");
        request.setPassword("secret");

        User user = User.builder()
                .email("alice@test.com")
                .password("hashed")
                .name("Alice")
                .role(User.Role.USER)
                .build();

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_invalidCredentials_throws() {
        LoginRequest request = new LoginRequest();
        request.setEmail("alice@test.com");
        request.setPassword("wrong");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}
