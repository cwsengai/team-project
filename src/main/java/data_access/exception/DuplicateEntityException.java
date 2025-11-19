package data_access.exception;

/**
 * Exception thrown when attempting to create an entity that already exists.
 * This typically occurs during insert operations when a unique constraint is violated.
 */
public class DuplicateEntityException extends RepositoryException {
    
    private final String entityType;
    private final String identifier;
    
    /**
     * Creates a new duplicate entity exception.
     *
     * @param entityType the type of entity (e.g., "Portfolio", "Company")
     * @param identifier the identifier that already exists
     */
    public DuplicateEntityException(String entityType, String identifier) {
        super(String.format("%s already exists with identifier: %s", entityType, identifier));
        this.entityType = entityType;
        this.identifier = identifier;
    }
    
    /**
     * Creates a new duplicate entity exception with a custom message.
     *
     * @param message the error message
     */
    public DuplicateEntityException(String message) {
        super(message);
        this.entityType = null;
        this.identifier = null;
    }
    
    /**
     * Get the entity type that was duplicated.
     *
     * @return the entity type
     */
    public String getEntityType() {
        return entityType;
    }
    
    /**
     * Get the identifier that caused the duplicate.
     *
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }
}
