package data_access;

/**
 * Exception thrown when a user attempts an operation they don't have permission for.
 * This typically occurs when Row Level Security (RLS) policies deny access.
 */
public class PermissionDeniedException extends RepositoryException {
    
    private final String operation;
    private final String resource;
    
    /**
     * Creates a new permission denied exception.
     *
     * @param operation the operation that was denied (e.g., "INSERT", "UPDATE", "DELETE")
     * @param resource the resource being accessed (e.g., "portfolios", "companies")
     */
    public PermissionDeniedException(String operation, String resource) {
        super(String.format("Permission denied: cannot %s on %s", operation, resource));
        this.operation = operation;
        this.resource = resource;
    }
    
    /**
     * Creates a new permission denied exception with a custom message.
     *
     * @param message the error message
     */
    public PermissionDeniedException(String message) {
        super(message);
        this.operation = null;
        this.resource = null;
    }
    
    /**
     * Get the operation that was denied.
     *
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }
    
    /**
     * Get the resource that was being accessed.
     *
     * @return the resource
     */
    public String getResource() {
        return resource;
    }
}
