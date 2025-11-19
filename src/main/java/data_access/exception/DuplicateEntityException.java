package data_access.exception;

/**
 * Exception thrown when attempting to create an entity that already exists.
 * This typically occurs during insert operations when a unique constraint is violated.
 */
public class DuplicateEntityException extends RepositoryException {
    
    private final String entityType;
    private final String identifier;
    
    public DuplicateEntityException(String entityType, String identifier) {
        super(String.format("%s already exists with identifier: %s", entityType, identifier));
        this.entityType = entityType;
        this.identifier = identifier;
    }
    
    public DuplicateEntityException(String message) {
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
