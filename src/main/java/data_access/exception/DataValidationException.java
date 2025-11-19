package data_access.exception;

/**
 * Exception thrown when data fails validation before being persisted.
 * This includes constraint violations, invalid formats, and business rule violations.
 */
public class DataValidationException extends RepositoryException {
    
    private final String fieldName;
    
    public DataValidationException(String message) {
        super(message);
        this.fieldName = null;
    }
    
    public DataValidationException(String fieldName, String message) {
        super(String.format("Validation failed for field '%s': %s", fieldName, message));
        this.fieldName = fieldName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
}
