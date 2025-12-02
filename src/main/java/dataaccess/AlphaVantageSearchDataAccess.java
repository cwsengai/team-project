package dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import entity.Company;
import usecase.search_company.SearchCompanyDataAccess;

public class AlphaVantageSearchDataAccess implements SearchCompanyDataAccess {
    private final List<Company> cachedCompanies;
    private final List<String> allTickers;
    private static final Map<String, String> COMPANY_NAMES = createCompanyNamesMap();

    public AlphaVantageSearchDataAccess(List<Company> companies) {
        this.cachedCompanies = companies != null ? new ArrayList<>(companies) : new ArrayList<>();
        this.allTickers = Top100Companies.getAll();
    }

    @Override
    public List<Company> searchCompanies(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(cachedCompanies);
        }

        String lowercaseQuery = query.toLowerCase().trim();

        // Search through loaded companies first
        List<Company> results = new ArrayList<>(cachedCompanies.stream()
                .filter(company ->
                        company.getName().toLowerCase().contains(lowercaseQuery)
                                || company.getSymbol().toLowerCase().contains(lowercaseQuery)
                )
                .collect(Collectors.toList()));

        // Search through ALL tickers (even not loaded)
        for (String ticker : allTickers) {
            String companyName = COMPANY_NAMES.getOrDefault(ticker, ticker);

            // Check if ticker OR name matches
            if (ticker.toLowerCase().contains(lowercaseQuery)
                    || companyName.toLowerCase().contains(lowercaseQuery)) {

                // Check if already in results
                boolean exists = results.stream()
                        .anyMatch(company -> company.getSymbol().equalsIgnoreCase(ticker));

                if (!exists) {
                    results.add(createMinimalCompany(ticker, companyName));
                }
            }
        }

        return results;
    }

    private Company createMinimalCompany(String ticker, String name) {
        return new Company(
                ticker,
                name,
                "", "", "", "",
                0L, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                List.of(), List.of()
        );
    }

    /**
     * Replaces the contents of the internal company cache with the provided list.
     * If the supplied list is {@code null}, the cache is simply cleared.
     *
     * @param companies the new list of companies to cache, or {@code null} to clear the cache
     */
    public void updateCache(List<Company> companies) {
        cachedCompanies.clear();
        if (companies != null) {
            cachedCompanies.addAll(companies);
        }
    }

    // ✅ Create the full company names map
    private static Map<String, String> createCompanyNamesMap() {
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
