package entity;

/**
 * Represents an economic indicator with its value and metadata.
 */
public class EconomicIndicator {
    private final String name;
    private final String value;
    private final String lastUpdated;

    public EconomicIndicator(String name, String value, String lastUpdated) {
        this.name = name;
        this.value = value;
        this.lastUpdated = lastUpdated;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

}
