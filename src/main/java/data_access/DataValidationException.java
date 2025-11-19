package data_access;

/**
 * Exception thrown when data fails validation before being persisted.
 * This includes constraint violations, invalid formats, and business rule violations.
 */
public class DataValidationException extends RepositoryException {
    
    private final String fieldName;
    
    /**
     * Creates a new data validation exception with a message.
     *
     * @param message the error message
     */
    public DataValidationException(String message) {
        super(message);
        this.fieldName = null;
    }
    
    /**
     * Creates a new data validation exception for a specific field.
     *
     * @param fieldName the field that failed validation
     * @param message the error message
     */
    public DataValidationException(String fieldName, String message) {
        super(String.format("Validation failed for field '%s': %s", fieldName, message));
        this.fieldName = fieldName;
    }
    
    /**
     * Get the field name that failed validation.
     *
     * @return the field name, or null if not specific to a field
     */
    public String getFieldName() {
        return fieldName;
    }
}
