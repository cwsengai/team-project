package usecase.session;

import java.util.UUID;

/**
 * Provides access to session-related data such as the JWT token and user ID.
 */
public interface SessionDataAccessInterface {

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
