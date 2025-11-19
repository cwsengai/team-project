package data_access;

/**
 * Exception thrown when there is a problem connecting to the database.
 * This includes network issues, authentication failures, and connection timeouts.
 */
public class DatabaseConnectionException extends RepositoryException {
    
    /**
     * Creates a new database connection exception with a message.
     *
     * @param message the error message
     */
    public DatabaseConnectionException(String message) {
        super(message);
    }
    
    /**
     * Creates a new database connection exception with a message and cause.
     *
     * @param message the error message
     * @param cause the underlying cause
     */
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
