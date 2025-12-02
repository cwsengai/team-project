package entity;

/**
 * Represents an economic indicator with its value and metadata.
 */
public class EconomicIndicator {
    private final String name;
    private final String value;
    private final String lastUpdated;
    private final String apiFunction;

    public EconomicIndicator(String name, String value, String lastUpdated, String apiFunction) {
        this.name = name;
        this.value = value;
        this.lastUpdated = lastUpdated;
        this.apiFunction = apiFunction;
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

    public String getApiFunction() {
        return apiFunction;
    }
}
