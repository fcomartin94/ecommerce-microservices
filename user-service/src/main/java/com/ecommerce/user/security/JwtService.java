package com.ecommerce.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT utility service for the user-service.
 *
 * <p>Handles token generation, validation, and claim extraction. Tokens are
 * signed with HMAC-SHA256 using a Base64-encoded secret read from
 * {@code jwt.secret}. Expiration is controlled by {@code jwt.expiration}
 * (milliseconds).</p>
 *
 * <p>Used by {@link JwtAuthFilter} to authenticate every incoming request
 * that carries an {@code Authorization: Bearer <token>} header.</p>
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Generates a JWT token with no extra claims for the given user.
     *
     * @param userDetails the authenticated user — subject is set to {@link UserDetails#getUsername()}
     * @return a signed, compact JWT string
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token with additional claims merged into the payload.
     *
     * @param extraClaims custom claims to include in the token body
     * @param userDetails the authenticated user
     * @return a signed, compact JWT string
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)                                          // claims adicionales (ej. roles)
                .setSubject(userDetails.getUsername())                           // "sub" = email del usuario
                .setIssuedAt(new Date())                                         // "iat" = timestamp actual
                .setExpiration(new Date(System.currentTimeMillis() + expiration))// "exp" = iat + 24h
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)             // firma HMAC-SHA256
                .compact();                                                      // serializar a Base64url
    }

    /**
     * Returns {@code true} if the token's subject matches the given user
     * and the token has not expired.
     *
     * @param token       the JWT string to validate
     * @param userDetails the user to validate against
     * @return {@code true} if the token is valid for this user
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // Doble verificación: el sujeto del token coincide con el usuario cargado de BD
        // Y el token no ha superado su fecha de expiración
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Extracts the subject (username / email) from the token.
     *
     * @param token a valid JWT string
     * @return the subject claim value
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Returns {@code true} if the token's expiration date is in the past. */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /** Extracts the expiration date claim from the token. */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic claim extractor — applies the given resolver function to the
     * parsed {@link Claims} object.
     *
     * @param <T>            the return type of the resolver
     * @param token          a valid JWT string
     * @param claimsResolver a function mapping {@link Claims} to the desired value
     * @return the extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        // Parsear el token completo y luego aplicar la función extractora al mapa de claims
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the JWT and returns all claims after verifying the signature.
     *
     * @param token a compact JWT string
     * @return the full {@link Claims} payload
     * @throws io.jsonwebtoken.JwtException if the token is malformed or the signature is invalid
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // usar la misma clave con la que se firmó
                .build()
                .parseClaimsJws(token)         // verificar firma y deserializar; lanza JwtException si inválido
                .getBody();                    // devolver solo el payload (claims), no la cabecera
    }

    /**
     * Decodes the Base64 secret and builds the HMAC-SHA signing key.
     *
     * @return a {@link Key} suitable for HS256 signing/verification
     */
    private Key getSigningKey() {
        // Decodificar el secreto de Base64 a bytes crudos
        byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(secretKey);
        // Crear una clave HMAC-SHA a partir de los bytes (requiere mínimo 256 bits = 32 bytes)
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
