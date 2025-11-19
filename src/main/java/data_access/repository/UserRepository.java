package data_access.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import entity.User;

/**
 * Repository interface for User entity persistence.
 * Provides methods for user authentication and profile management.
 */
public interface UserRepository {
    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    User save(User user);

    void updateLastLogin(String userId, LocalDateTime timestamp);
}
