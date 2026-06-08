package com.ecommerce.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter that runs once per request.
 *
 * <p>Intercepts every incoming HTTP request and, if a valid
 * {@code Authorization: Bearer <token>} header is present, extracts the
 * user email from the token, loads the corresponding {@link UserDetails},
 * and sets the authentication in the {@link SecurityContextHolder}.</p>
 *
 * <p>Requests without a {@code Bearer} header, or with an invalid/expired
 * token, pass through without authentication — the downstream security rules
 * in {@link com.ecommerce.user.config.SecurityConfig} then decide whether
 * to allow or reject them.</p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Core filter logic executed once per request.
     *
     * <ol>
     *   <li>Reads the {@code Authorization} header; skips if missing or not {@code Bearer}.</li>
     *   <li>Extracts the user email from the JWT.</li>
     *   <li>If no authentication is set yet, loads {@link UserDetails} and validates the token.</li>
     *   <li>On success, populates the {@link SecurityContextHolder} and continues the chain.</li>
     * </ol>
     *
     * @param request     the incoming HTTP request
     * @param response    the HTTP response
     * @param filterChain the remaining filter chain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Leer el header Authorization de la petición entrante
        final String authHeader = request.getHeader("Authorization");

        // 2. Si no hay header o no empieza por "Bearer ", no hay token que validar.
        //    Dejar que la petición pase — SecurityConfig decidirá si el endpoint requiere auth.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraer el token JWT quitando el prefijo "Bearer " (7 caracteres)
        final String jwt = authHeader.substring(7);

        // 4. Parsear el sujeto (email del usuario) del payload del JWT sin verificar aún la firma
        final String userEmail = jwtService.extractUsername(jwt);

        // 5. Solo procesar si tenemos email Y el SecurityContext no tiene ya una autenticación
        //    (evita reautenticar en cada filtro de la cadena si ya se procesó antes)
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Cargar el UserDetails desde la BD (necesario para verificar firma y roles)
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            // 7. Verificar firma HS256 + que el token no haya expirado
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 8. Construir el objeto de autenticación con los authorities del usuario.
                //    credentials = null porque ya no necesitamos la contraseña en este punto.
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                // 9. Enriquecer con detalles de la request (IP, session id) para auditoría
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 10. Registrar la autenticación en el contexto de seguridad del hilo actual
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 11. Continuar con el siguiente filtro de la cadena (sea cual sea el resultado)
        filterChain.doFilter(request, response);
    }
}