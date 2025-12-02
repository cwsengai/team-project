package interfaceadapter.company_list;

/**
 * Utility class for formatting financial data for presentation.
 * Handles market capitalization and P/E ratio formatting.
 *
 * Location: Interface Adapter Layer (company_list package)
 * Purpose: Format data for CompanyDisplayData
 */
public class DataFormatters {

    /**
     * Format market capitalization in human-readable format.
     * @param marketCap The market cap value
     * @return Formatted string (e.g., "$2.5T", "$50.0B", "$500.0M")
     */
    public static String formatMarketCap(double marketCap) {
        if (marketCap >= 1_000_000_000_000.0) {
            return String.format("$%.1fT", marketCap / 1_000_000_000_000.0);
        } else if (marketCap >= 1_000_000_000.0) {
            return String.format("$%.1fB", marketCap / 1_000_000_000.0);
        } else if (marketCap >= 1_000_000.0) {
            return String.format("$%.1fM", marketCap / 1_000_000.0);
        } else {
            return String.format("$%.0f", marketCap);
        }
    }

    /**
     * Format P/E ratio for display.
     * @param peRatio The P/E ratio value
     * @return Formatted string (e.g., "28.50") or "N/A" if invalid
     */
    public static String formatPeRatio(double peRatio) {
        if (peRatio <= 0) {
            return "N/A";
        }
        return String.format("%.2f", peRatio);
    }
}
