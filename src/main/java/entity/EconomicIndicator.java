package entity;

/**
 * Represents an economic indicator with its value and metadata.
 * @param name        the name of the economic indicator
 * @param value       the value of the economic indicator
 * @param lastUpdated the last updated timestamp of the indicator
 */
public record EconomicIndicator(String name, String value, String lastUpdated) {

}
