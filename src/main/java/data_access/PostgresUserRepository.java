package data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import entity.User;

/**
 * PostgreSQL implementation of UserRepository.
 * Manages user authentication and profile data.
 * Note: Password hashing is handled separately for security.
 */
public class PostgresUserRepository implements UserRepository {
    private final PostgresClient client;

    public PostgresUserRepository() {
        this.client = new PostgresClient();
    }

    @Override
    public Optional<User> findById(String id) {
        String sql = "SELECT id, email, display_name, created_at, last_login " +
                     "FROM users WHERE id = ?::uuid";

        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, email, display_name, created_at, last_login " +
                     "FROM users WHERE email = ?";

        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            return insert(user);
        } else {
            return update(user);
        }
    }

    private User insert(User user) {
        String sql = "INSERT INTO users (email, password_hash, display_name) " +
                     "VALUES (?, '', ?) " +
                     "RETURNING id, email, display_name, created_at, last_login";

        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getDisplayName());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            throw new RuntimeException("Failed to insert user");
        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate") || e.getMessage().contains("unique")) {
                throw new RuntimeException("User with email '" + user.getEmail() + "' already exists", e);
            }
            throw new RuntimeException("Error inserting user: " + e.getMessage(), e);
        }
    }

    private User update(User user) {
        String sql = "UPDATE users " +
                     "SET display_name = ? " +
                     "WHERE id = ?::uuid " +
                     "RETURNING id, email, display_name, created_at, last_login";

        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getDisplayName());
            stmt.setString(2, user.getId());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            throw new RuntimeException("User not found: " + user.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateLastLogin(String userId, LocalDateTime timestamp) {
        String sql = "UPDATE users " +
                     "SET last_login = ? " +
                     "WHERE id = ?::uuid";

        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(timestamp));
            stmt.setString(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("User not found: " + userId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating last login: " + e.getMessage(), e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        Timestamp lastLoginTs = rs.getTimestamp("last_login");
        LocalDateTime lastLogin = lastLoginTs != null ? lastLoginTs.toLocalDateTime() : null;
        
        return new User(
            rs.getString("id"),
            rs.getString("email"),
            rs.getString("display_name"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            lastLogin
        );
    }
}
