package data_access.exception;

/**
 * Exception thrown when a requested entity cannot be found in the repository.
 * This typically occurs when querying by ID or unique identifier.
 */
public class EntityNotFoundException extends RepositoryException {
    
    private final String entityType;
    private final String identifier;
    
    public EntityNotFoundException(String entityType, String identifier) {
        super(String.format("%s not found with identifier: %s", entityType, identifier));
        this.entityType = entityType;
        this.identifier = identifier;
    }
    
    public EntityNotFoundException(String message) {
        super(message);
        this.entityType = null;
        this.identifier = null;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public String getIdentifier() {
        return identifier;
    }
}
