package com.ecommerce.user.config;

import com.ecommerce.user.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration for the User Service.
 *
 * <p>Configures a stateless, JWT-based security filter chain. CSRF is disabled
 * (stateless API), sessions are never created, and every request must carry a
 * valid {@code Authorization: Bearer <token>} header — except for the public
 * endpoints listed below.</p>
 *
 * <h3>Public endpoints (no JWT required)</h3>
 * <ul>
 *   <li>{@code POST /api/users/register}</li>
 *   <li>{@code POST /api/users/login}</li>
 *   <li>{@code GET  /api/users/health}</li>
 *   <li>{@code /h2-console/**} (development only)</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Builds the main {@link SecurityFilterChain}.
     *
     * <p>Registers {@link JwtAuthFilter} before Spring's default
     * {@link UsernamePasswordAuthenticationFilter} so that JWT tokens
     * are validated on every request.</p>
     *
     * @param http                   the HttpSecurity builder
     * @param authenticationProvider DAO provider wired with {@link UserDetailsServiceConfig}
     * @return the configured filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider
    ) throws Exception {
        http
            // CSRF desactivado: no usamos cookies de sesión, así que no hay vulnerabilidad CSRF
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos: no requieren token JWT
                .requestMatchers("/api/users/auth/**",
                                 "/api/users/health",
                                 "/api/users/register",
                                 "/api/users/login",
                                 "/h2-console/**").permitAll()
                // Cualquier otra ruta requiere autenticación válida
                .anyRequest().authenticated()
            )
            // Sin sesiones HTTP: cada petición se autentica de forma independiente con el JWT
            .sessionManagement(sess -> sess
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // H2 console usa iframes; frameOptions sameOrigin por defecto los bloquea
            .headers(headers -> headers.frameOptions(f -> f.disable()))
            // Registrar el proveedor que usa UserDetailsService + BCrypt
            .authenticationProvider(authenticationProvider)
            // Nuestro filtro JWT debe ejecutarse ANTES que el filtro de autenticación por formulario
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Wires a {@link DaoAuthenticationProvider} with the application's
     * {@code UserDetailsService} and BCrypt password encoder.
     *
     * @param userDetailsService bean defined in {@link UserDetailsServiceConfig}
     * @return a configured authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider(org.springframework.security.core.userdetails.UserDetailsService userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // Indicar que use nuestro UserDetailsService (que busca en H2 por email)
        provider.setUserDetailsService(userDetailsService);
        // Indicar que use BCrypt para comparar la contraseña recibida con el hash almacenado
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Exposes the {@link AuthenticationManager} as a Spring bean so it can be
     * injected into {@link com.ecommerce.user.service.AuthService} for programmatic
     * authentication during login.
     *
     * @param config Spring's authentication configuration
     * @return the application's {@link AuthenticationManager}
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Creates a {@link BCryptPasswordEncoder} bean used for password hashing
     * at registration time and verification at login time.
     *
     * @return a BCrypt password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}