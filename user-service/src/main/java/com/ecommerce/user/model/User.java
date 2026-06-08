package com.ecommerce.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JPA entity representing a registered user.
 *
 * <p>Persisted in the H2 in-memory {@code users} table. The email field
 * serves as the unique identifier for authentication (Spring Security
 * {@code username}). Passwords are always stored as BCrypt hashes —
 * never in plain text.</p>
 *
 * <p>The {@link Role} enum controls endpoint access; all self-registered
 * users receive the {@code USER} role by default.</p>
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        USER, ADMIN
    }
}