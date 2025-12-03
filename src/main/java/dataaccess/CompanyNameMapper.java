package dataaccess;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps ticker symbols to company names.
 * Provides display names for companies when full data isn't loaded from API.
 *
 * <p></p>
 * Location: Data Access Layer
 * Purpose: Provides company name data without API calls
 */
public class CompanyNameMapper {

    private static final Map<String, String> COMPANY_NAMES = createNameMap();

    /**
     * Get company name from ticker symbol.
     * @param ticker The ticker symbol (e.g., "AAPL")
     * @return The company name (e.g., "Apple Inc") or the ticker if not found
     */
    public static String getCompanyName(String ticker) {
        return COMPANY_NAMES.getOrDefault(ticker, ticker);
    }

    /**
     * Creates and returns a mapping of all 100 company ticker symbols to
     * their corresponding full company names.
     *
     * @return a map where each key is a stock ticker and each value is its full company name
     */
    private static Map<String, String> createNameMap() {
        Map<String, String> names = new HashMap<>();

        // Top 20
        names.put("AAPL", "Apple Inc");
        names.put("MSFT", "Microsoft Corporation");
        names.put("GOOGL", "Alphabet Inc");
        names.put("AMZN", "Amazon.com Inc");
        names.put("NVDA", "NVIDIA Corporation");
        names.put("META", "Meta Platforms Inc");
        names.put("TSLA", "Tesla Inc");
        names.put("BRK.B", "Berkshire Hathaway");
        names.put("V", "Visa Inc");
        names.put("UNH", "UnitedHealth Group");
        names.put("JNJ", "Johnson & Johnson");
        names.put("WMT", "Walmart Inc");
        names.put("JPM", "JPMorgan Chase");
        names.put("MA", "Mastercard Inc");
        names.put("XOM", "Exxon Mobil");
        names.put("PG", "Procter & Gamble");
        names.put("HD", "Home Depot");
        names.put("CVX", "Chevron Corporation");
        names.put("AVGO", "Broadcom Inc");
        names.put("MRK", "Merck & Co");

        // 21-40
        names.put("ABBV", "AbbVie Inc");
        names.put("PEP", "PepsiCo Inc");
        names.put("KO", "Coca-Cola Company");
        names.put("COST", "Costco Wholesale");
        names.put("ADBE", "Adobe Inc");
        names.put("TMO", "Thermo Fisher Scientific");
        names.put("MCD", "McDonald's Corporation");
        names.put("CSCO", "Cisco Systems");
        names.put("ACN", "Accenture");
        names.put("ABT", "Abbott Laboratories");
        names.put("NKE", "Nike Inc");
        names.put("LLY", "Eli Lilly");
        names.put("TXN", "Texas Instruments");
        names.put("DHR", "Danaher Corporation");
        names.put("CRM", "Salesforce Inc");
        names.put("NEE", "NextEra Energy");
        names.put("DIS", "Walt Disney Company");
        names.put("VZ", "Verizon Communications");
        names.put("CMCSA", "Comcast Corporation");
        names.put("ORCL", "Oracle Corporation");

        // 41-60
        names.put("INTC", "Intel Corporation");
        names.put("NFLX", "Netflix Inc");
        names.put("AMD", "Advanced Micro Devices");
        names.put("PFE", "Pfizer Inc");
        names.put("PM", "Philip Morris International");
        names.put("T", "AT&T Inc");
        names.put("UPS", "United Parcel Service");
        names.put("BA", "Boeing Company");
        names.put("IBM", "IBM");
        names.put("QCOM", "Qualcomm Inc");
        names.put("HON", "Honeywell International");
        names.put("AMGN", "Amgen Inc");
        names.put("RTX", "Raytheon Technologies");
        names.put("UNP", "Union Pacific");
        names.put("SPGI", "S&P Global");
        names.put("LOW", "Lowe's Companies");
        names.put("CAT", "Caterpillar Inc");
        names.put("SBUX", "Starbucks Corporation");
        names.put("GS", "Goldman Sachs");
        names.put("INTU", "Intuit Inc");

        // 61-80
        names.put("AXP", "American Express");
        names.put("CVS", "CVS Health");
        names.put("DE", "Deere & Company");
        names.put("BLK", "BlackRock Inc");
        names.put("MDLZ", "Mondelez International");
        names.put("GILD", "Gilead Sciences");
        names.put("ADP", "Automatic Data Processing");
        names.put("MMM", "3M Company");
        names.put("TJX", "TJX Companies");
        names.put("BKNG", "Booking Holdings");
        names.put("ISRG", "Intuitive Surgical");
        names.put("AMT", "American Tower");
        names.put("REGN", "Regeneron Pharmaceuticals");
        names.put("CI", "Cigna Corporation");
        names.put("VRTX", "Vertex Pharmaceuticals");
        names.put("CB", "Chubb Limited");
        names.put("MO", "Altria Group");
        names.put("SYK", "Stryker Corporation");
        names.put("ZTS", "Zoetis Inc");
        names.put("BDX", "Becton Dickinson");

        // 81-100
        names.put("TGT", "Target Corporation");
        names.put("SO", "Southern Company");
        names.put("USB", "U.S. Bancorp");
        names.put("PLD", "Prologis Inc");
        names.put("DUK", "Duke Energy");
        names.put("CME", "CME Group");
        names.put("CSX", "CSX Corporation");
        names.put("CL", "Colgate-Palmolive");
        names.put("ITW", "Illinois Tool Works");
        names.put("NSC", "Norfolk Southern");
        names.put("APD", "Air Products and Chemicals");
        names.put("EOG", "EOG Resources");
        names.put("WM", "Waste Management");
        names.put("SHW", "Sherwin-Williams");
        names.put("MCO", "Moody's Corporation");
        names.put("CCI", "Crown Castle");
        names.put("EL", "Estée Lauder");
        names.put("SCHW", "Charles Schwab");
        names.put("AON", "Aon plc");
        names.put("HUM", "Humana Inc");

        return names;
    }
}
