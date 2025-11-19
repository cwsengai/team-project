package data_access;

import java.time.LocalDateTime;
import java.util.Optional;

import entity.User;

/**
 * Repository interface for User entity persistence.
 * Provides methods for user authentication and profile management.
 */
public interface UserRepository {
    /**
     * Find a user by their unique ID.
     *
     * @param id the user ID (UUID)
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findById(String id);

    /**
     * Find a user by their email address.
     *
     * @param email the user's email
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Save or update a user.
     *
     * @param user the user to save
     * @return the saved user with generated ID if new
     */
    User save(User user);

    /**
     * Update the last login timestamp for a user.
     *
     * @param userId the user ID
     * @param timestamp the login timestamp
     */
    void updateLastLogin(String userId, LocalDateTime timestamp);
}
