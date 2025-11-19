package data_access.exception;

/**
 * Base exception for repository layer errors.
 * All repository-specific exceptions should extend this class.
 */
public class RepositoryException extends RuntimeException {
    
    public RepositoryException(String message) {
        super(message);
    }
    
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
