package data_access;

import entity.Company;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Supabase implementation of CompanyRepository.
 * Uses REST API to interact with the companies table.
 * 
 * Note: Company data is typically read-only for regular users.
 * Only admins (using service role key) can insert/update companies.
 */
public class SupabaseCompanyRepository implements CompanyRepository {
    private final SupabaseClient client;

    /**
     * Creates a new Supabase company repository.
     *
     * @param client the authenticated Supabase client
     */
    public SupabaseCompanyRepository(SupabaseClient client) {
        this.client = client;
    }

    @Override
    public Optional<Company> findByTicker(String ticker) {
        try {
            // Query: GET /rest/v1/companies?ticker=eq.{ticker}
            Company[] companies = client.queryWithFilter(
                "companies",
                "ticker=eq." + ticker,
                Company[].class
            );

            if (companies != null && companies.length > 0) {
                return Optional.of(companies[0]);
            }
            return Optional.empty();

        } catch (IOException e) {
            throw new RuntimeException("Error fetching company by ticker: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Company> findById(String id) {
        try {
            // Query: GET /rest/v1/companies?id=eq.{id}
            Company[] companies = client.queryWithFilter(
                "companies",
                "id=eq." + id,
                Company[].class
            );

            if (companies != null && companies.length > 0) {
                return Optional.of(companies[0]);
            }
            return Optional.empty();

        } catch (IOException e) {
            throw new RuntimeException("Error fetching company by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Company> findBySector(String sector) {
        try {
            // Query: GET /rest/v1/companies?sector=eq.{sector}
            Company[] companies = client.queryWithFilter(
                "companies",
                "sector=eq." + sector,
                Company[].class
            );

            return companies != null ? Arrays.asList(companies) : Collections.emptyList();

        } catch (IOException e) {
            throw new RuntimeException("Error fetching companies by sector: " + e.getMessage(), e);
        }
    }

    @Override
    public Company save(Company company) {
        try {
            // Check if company exists
            if (company.getSymbol() != null && !company.getSymbol().isEmpty()) {
                // Check if exists and update by symbol (using ticker as alias)
                Optional<Company> existing = findByTicker(company.getSymbol());
                if (existing.isPresent()) {
                    return update(company);
                }
            }
            
            // Insert new company
            return insert(company);

        } catch (IOException e) {
            throw new RuntimeException("Error saving company: " + e.getMessage(), e);
        }
    }

    private Company insert(Company company) throws IOException {
        // Note: This may fail if user doesn't have INSERT permission
        // Only service role or admin should insert companies
        Company[] result = client.insert(
            "companies",
            company,
            Company[].class
        );

        if (result != null && result.length > 0) {
            return result[0];
        }
        throw new RuntimeException("Insert failed: no data returned");
    }

    private Company update(Company company) throws IOException {
        // Note: This may fail if user doesn't have UPDATE permission
        Company[] result = client.update(
            "companies",
            "symbol=eq." + company.getSymbol(),
            company,
            Company[].class
        );

        if (result != null && result.length > 0) {
            return result[0];
        }
        throw new RuntimeException("Update failed: company not found");
    }

    @Override
    public void saveAll(List<Company> companies) {
        // Note: Supabase supports bulk insert via POST with JSON array
        // For simplicity, we'll insert one at a time
        // In production, optimize this by using bulk insert
        for (Company company : companies) {
            save(company);
        }
    }
}
