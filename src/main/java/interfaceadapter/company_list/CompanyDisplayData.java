package interfaceadapter.company_list;

/**
 * Data structure for displaying company information in the UI.
 * This is a simple DTO (Data Transfer Object) for the view layer.
 */
public record CompanyDisplayData(String symbol, String name, String country, String formattedMarketCap,
                                 String formattedPeRatio) {
}
