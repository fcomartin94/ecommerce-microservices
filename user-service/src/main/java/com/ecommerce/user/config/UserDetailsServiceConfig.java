package com.ecommerce.user.config;

import com.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Spring bean configuration for {@link UserDetailsService}.
 *
 * <p>Bridges Spring Security's authentication mechanism with the
 * application's own {@link com.ecommerce.user.model.User} model stored in H2.
 * The {@link UserDetailsService} bean is consumed by both
 * {@link SecurityConfig} (to wire the {@code DaoAuthenticationProvider})
 * and {@link com.ecommerce.user.security.JwtAuthFilter} (to load user data
 * during token validation).</p>
 *
 * <p>Kept in a separate configuration class to avoid circular bean
 * dependency between {@link SecurityConfig} and {@link UserDetailsService}.</p>
 */
@Configuration
@RequiredArgsConstructor
public class UserDetailsServiceConfig {

    private final UserRepository userRepository;

    /**
     * Looks up a user by email (used as the Spring Security {@code username})
     * and maps it to a Spring Security {@link org.springframework.security.core.userdetails.User}
     * with the application role assigned.
     *
     * @return a {@link UserDetailsService} backed by {@link UserRepository}
     * @throws UsernameNotFoundException if no user with the given email exists
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Spring Security llama a este lambda con el "username" que el usuario escribió.
        // En nuestra app el "username" de Spring Security ES el email del usuario.
        return username -> userRepository.findByEmail(username)
                // Mapear nuestra entidad User (JPA) al User de Spring Security (auth).
                // Nota: usamos el builder de Spring Security, no el nuestro,
                // porque JwtService y DaoAuthenticationProvider esperan la interfaz UserDetails.
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())       // hash BCrypt ya almacenado
                        .roles(user.getRole().name())       // "USER" o "ADMIN"
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}

