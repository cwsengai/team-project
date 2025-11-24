package data_access;

import entity.Company;
import use_case.company.CompanyGateway;
import use_case.company_list.CompanyListDataAccess;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access implementation for Company List use case.
 * Fetches top 100 companies from Alpha Vantage API.
 *
 * Note: Alpha Vantage has rate limits (5 API calls/minute for free tier).
 * For production, consider:
 * 1. Caching company data in a database
 * 2. Using a paid API tier
 * 3. Batch fetching during off-peak hours
 */
public class AlphaVantageCompanyListDataAccess implements CompanyListDataAccess {
    private final CompanyGateway gateway;
    private final boolean useSampleData;  // Use sample to avoid rate limits during development

    /**
     * Constructor for production use (fetches all 100 companies).
     */
    public AlphaVantageCompanyListDataAccess(CompanyGateway gateway) {
        this(gateway, true); // Default to sample data for development
    }

    /**
     * Constructor with option to use sample data.
     * @param gateway Company gateway for API calls
     * @param useSampleData If true, only fetch top 10 companies (for testing)
     */
    public AlphaVantageCompanyListDataAccess(CompanyGateway gateway, boolean useSampleData) {
        this.gateway = gateway;
        this.useSampleData = useSampleData;
    }

    @Override
    public List<Company> getCompanyList() {
        List<String> tickers = useSampleData
                ? Top100Companies.getSample(20)  // Use top 10 for development
                : Top100Companies.getAll();       // Use all 100 for production

        List<Company> companies = new ArrayList<>();

        // Fetch each company's data from the API
        for (String ticker : tickers) {
            try {
                Company company = gateway.fetchOverview(ticker);
                if (company != null && company.getMarketCapitalization() > 0) {
                    companies.add(company);
                }

                // Add a small delay to respect API rate limits (5 calls/minute for free tier)
                // For free tier: 12 seconds between calls
                // For premium: can remove or reduce this delay
                if (useSampleData) {
                    Thread.sleep(12000); // 12 seconds = safe for free tier
                } else {
                    Thread.sleep(12000); // Adjust based on your API tier
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Interrupted while fetching company: " + ticker);
            } catch (Exception e) {
                System.err.println("Error fetching company " + ticker + ": " + e.getMessage());
                // Continue with next company
            }
        }

        return companies;
    }
}