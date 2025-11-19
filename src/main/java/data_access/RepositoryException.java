package data_access;

/**
 * Base exception for repository layer errors.
 * All repository-specific exceptions should extend this class.
 */
public class RepositoryException extends RuntimeException {
    
    /**
     * Creates a new repository exception with a message.
     *
     * @param message the error message
     */
    public RepositoryException(String message) {
        super(message);
    }
    
    /**
     * Creates a new repository exception with a message and cause.
     *
     * @param message the error message
     * @param cause the underlying cause
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
