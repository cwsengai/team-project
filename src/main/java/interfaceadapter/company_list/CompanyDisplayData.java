package interfaceadapter.company_list;

/**
 * Data structure for displaying company information in the UI.
 * This is a simple DTO (Data Transfer Object) for the view layer.
 */
public class CompanyDisplayData {
    private final String symbol;
    private final String name;
    private final String country;
    private final String formattedMarketCap;
    private final String formattedPeRatio;

    public CompanyDisplayData(String symbol, String name, String country,
                              String formattedMarketCap, String formattedPeRatio) {
        this.symbol = symbol;
        this.name = name;
        this.country = country;
        this.formattedMarketCap = formattedMarketCap;
        this.formattedPeRatio = formattedPeRatio;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getFormattedMarketCap() {
        return formattedMarketCap;
    }

    public String getFormattedPeRatio() {
        return formattedPeRatio;
    }
}