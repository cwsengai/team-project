package usecase.session;

import java.util.UUID;

/**
 * Provides access to session-related data such as the JWT token and user ID.
 */
public interface SessionDataAccessInterface {

    /**
     * Returns the current user's JWT token.
     *
     * @return the JWT token string, or {@code null} if the user is not logged in
     */
    String getJwtToken();

    /**
     * Sets the current user's JWT token.
     *
     * @param jwtToken the JWT token to store for the current user
     */
    void setJwtToken(String jwtToken);

    /**
     * Returns the UUID of the current logged-in user.
     *
     * @return the user's UUID, or {@code null} if no user is logged in
     */
    UUID getCurrentUserId();
}
