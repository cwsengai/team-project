package interface_adapter.company_list;

/**
 * Display data for a single company in the list view.
 * Contains formatted strings ready for UI display.
 */
public class CompanyDisplayData {
    private final String symbol;              // "AAPL"
    private final String name;                // "Apple Inc"
    private final String country;             // "United States"
    private final String formattedMarketCap;  // "$2.8T"
    private final String formattedPeRatio;    // "28.54"

    public CompanyDisplayData(String symbol,
                              String name,
                              String country,
                              String formattedMarketCap,
                              String formattedPeRatio) {
        this.symbol = symbol;
        this.name = name;
        this.country = country;
        this.formattedMarketCap = formattedMarketCap;
        this.formattedPeRatio = formattedPeRatio;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public String getCountry() { return country; }
    public String getFormattedMarketCap() { return formattedMarketCap; }
    public String getFormattedPeRatio() { return formattedPeRatio; }
}