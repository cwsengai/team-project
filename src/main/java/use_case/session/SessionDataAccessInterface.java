package use_case.session;

/**
 * Provides access to session data (e.g., JWT token) for the current user.
 */
import java.util.UUID;

public interface SessionDataAccessInterface {
    /**
     * Returns the current user's JWT token, or null if not logged in.
     */
    String getJwtToken();

    /**
     * Sets the current user's JWT token (e.g., after login).
     */
    void setJwtToken(String jwtToken);

    /**
     * Returns the current user's UUID (decoded from JWT or stored directly).
     */
    UUID getCurrentUserId();
}
