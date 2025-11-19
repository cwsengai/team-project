package data_access.exception;

/**
 * Exception thrown when a user attempts an operation they don't have permission for.
 * This typically occurs when Row Level Security (RLS) policies deny access.
 */
public class PermissionDeniedException extends RepositoryException {
    
    private final String operation;
    private final String resource;
    
    public PermissionDeniedException(String operation, String resource) {
        super(String.format("Permission denied: cannot %s on %s", operation, resource));
        this.operation = operation;
        this.resource = resource;
    }
    
    public PermissionDeniedException(String message) {
        super(message);
        this.operation = null;
        this.resource = null;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getResource() {
        return resource;
    }
}
