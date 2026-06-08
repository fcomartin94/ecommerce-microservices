package com.ecommerce.user.repository;

import com.ecommerce.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * JPA repository for {@link com.ecommerce.user.model.User} entities.
 *
 * <p>Backed by the H2 in-memory {@code users} table. Provides email-based
 * lookup methods used by the authentication flow and duplicate-email validation.</p>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * Used during login and JWT token validation.
     *
     * @param email the user's email
     * @return an {@link Optional} containing the user, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user with the given email already exists.
     * Used during registration to prevent duplicate accounts.
     *
     * @param email the email to check
     * @return {@code true} if a user with this email exists
     */
    boolean existsByEmail(String email);
}