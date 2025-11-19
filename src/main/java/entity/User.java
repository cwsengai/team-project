package entity;

import java.time.LocalDateTime;

/**
 * Represents a user in the system.
 * Note: Password is NOT stored in this entity for security reasons.
 * Password handling is done in the data access layer only.
 */
public class User {
    private final String id;
    private final String email;
    private String displayName;
    private final LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public User(String id, String email, String displayName, LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    public User(String id, String email, String displayName) {
        this(id, email, displayName, LocalDateTime.now(), null);
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * Update last login to current time.
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
}
