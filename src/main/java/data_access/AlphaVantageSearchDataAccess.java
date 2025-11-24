package data_access;

import entity.Company;
import use_case.search_company.SearchCompanyDataAccess;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data access implementation for Company Search use case.
 * Searches through the cached list of companies locally (no API calls).
 *
 * This approach:
 * 1. Avoids hitting API rate limits during search
 * 2. Provides instant search results
 * 3. Works offline once companies are loaded
 */
public class AlphaVantageSearchDataAccess implements SearchCompanyDataAccess {
    private final List<Company> cachedCompanies;

    /**
     * Constructor that takes a pre-loaded list of companies.
     * @param companies List of companies to search through
     */
    public AlphaVantageSearchDataAccess(List<Company> companies) {
        this.cachedCompanies = companies != null ? companies : new ArrayList<>();
    }

    @Override
    public List<Company> searchCompanies(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(cachedCompanies);
        }

        String lowercaseQuery = query.toLowerCase().trim();

        // Search by company name or ticker symbol
        return cachedCompanies.stream()
                .filter(company ->
                        company.getName().toLowerCase().contains(lowercaseQuery) ||
                                company.getSymbol().toLowerCase().contains(lowercaseQuery)
                )
                .collect(Collectors.toList());
    }

    /**
     * Update the cached companies list.
     * Call this when new companies are loaded.
     */
    public void updateCache(List<Company> companies) {
        cachedCompanies.clear();
        if (companies != null) {
            cachedCompanies.addAll(companies);
        }
    }

    /**
     * Get all cached companies.
     */
    public List<Company> getAllCompanies() {
        return new ArrayList<>(cachedCompanies);
    }
}