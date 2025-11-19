package data_access.exception;

/**
 * Exception thrown when there is a problem connecting to the database.
 * This includes network issues, authentication failures, and connection timeouts.
 */
public class DatabaseConnectionException extends RepositoryException {
    
    public DatabaseConnectionException(String message) {
        super(message);
    }
    
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
