package interfaceadapter.company_list;

/**
 * Data structure for displaying company information in the UI.
 * This is a simple DTO (Data Transfer Object) for the view layer.
 * @param symbol               the stock ticker symbol
 * @param name                 the company name
 * @param country              the country where the company is based
 * @param formattedMarketCap   the market capitalization formatted as a string
 * @param formattedPeRatio     the P/E ratio formatted as a string
 */
public record CompanyDisplayData(String symbol, String name, String country, String formattedMarketCap,
                                 String formattedPeRatio) {
}
