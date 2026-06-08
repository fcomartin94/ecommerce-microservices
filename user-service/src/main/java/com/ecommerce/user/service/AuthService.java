package com.ecommerce.user.service;

import com.ecommerce.user.dto.AuthResponse;
import com.ecommerce.user.dto.LoginRequest;
import com.ecommerce.user.dto.RegisterRequest;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Core authentication service handling user registration and login.
 *
 * <p>Registration encodes the password with BCrypt, persists the user,
 * and immediately issues a signed JWT. Login delegates credential verification
 * to Spring Security's {@link AuthenticationManager} (which triggers the
 * {@link com.ecommerce.user.config.UserDetailsServiceConfig} bean) and then
 * issues a JWT on success.</p>
 *
 * <p>Tokens are signed with HS256 using the secret and expiration configured
 * in {@code application.yml} ({@code jwt.secret}, {@code jwt.expiration}).</p>
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user account and issues a JWT token.
     *
     * @param request the registration payload (name, email, password)
     * @return an {@link AuthResponse} with the signed JWT, email, name, and role
     * @throws RuntimeException if a user with the given email already exists
     */
    public AuthResponse register(RegisterRequest request) {
        // Validación de unicidad: lanzar antes de tocar la BD para evitar constraint violations
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }

        // Construir la entidad. La contraseña se hashea con BCrypt aquí —
        // nunca se almacena el texto plano en la BD.
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // hash BCrypt
                .role(User.Role.USER)                                    // todos los auto-registros son USER
                .build();

        // Persistir el usuario en H2
        userRepository.save(user);

        // Adaptar la entidad al contrato UserDetails que espera JwtService.
        // Se usa el builder de Spring Security (no el nuestro) para no acoplar JwtService a User.
        String token = jwtService.generateToken(
            org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build()
        );

        // Devolver el token junto con la info pública del usuario (nunca el hash de contraseña)
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }

    /**
     * Authenticates an existing user and issues a JWT token.
     *
     * <p>Delegates credential verification to {@link AuthenticationManager};
     * throws {@link org.springframework.security.authentication.BadCredentialsException}
     * if the email/password pair is invalid.</p>
     *
     * @param request the login payload (email, password)
     * @return an {@link AuthResponse} with the signed JWT, email, name, and role
     */
    public AuthResponse login(LoginRequest request) {
        // Delegar la verificación de credenciales al AuthenticationManager de Spring Security.
        // Internamente llama a UserDetailsService.loadUserByUsername() + passwordEncoder.matches().
        // Si las credenciales no son correctas, lanza BadCredentialsException (→ HTTP 401).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Las credenciales son válidas — cargar el usuario completo de BD para construir la respuesta
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Generar un nuevo JWT para esta sesión
        String token = jwtService.generateToken(
            org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build()
        );

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
}