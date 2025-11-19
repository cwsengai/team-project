package data_access.exception;

/**
 * Exception thrown when a requested entity cannot be found in the repository.
 * This typically occurs when querying by ID or unique identifier.
 */
public class EntityNotFoundException extends RepositoryException {
    
    private final String entityType;
    private final String identifier;
    
    /**
     * Creates a new entity not found exception.
     *
     * @param entityType the type of entity (e.g., "Portfolio", "Company")
     * @param identifier the identifier used to search (e.g., ID or ticker)
     */
    public EntityNotFoundException(String entityType, String identifier) {
        super(String.format("%s not found with identifier: %s", entityType, identifier));
        this.entityType = entityType;
        this.identifier = identifier;
    }
    
    /**
     * Creates a new entity not found exception with a custom message.
     *
     * @param message the error message
     */
    public EntityNotFoundException(String message) {
        super(message);
        this.entityType = null;
        this.identifier = null;
    }
    
    /**
     * Get the entity type that was not found.
     *
     * @return the entity type
     */
    public String getEntityType() {
        return entityType;
    }
    
    /**
     * Get the identifier that was used in the search.
     *
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }
}
