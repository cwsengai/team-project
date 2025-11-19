package data_access.repository.supabase;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import data_access.client.SupabaseClient;
import data_access.exception.DataValidationException;
import data_access.exception.DatabaseConnectionException;
import data_access.exception.EntityNotFoundException;
import data_access.exception.PermissionDeniedException;
import data_access.exception.RepositoryException;
import data_access.repository.CompanyRepository;
import entity.Company;

/**
 * Supabase implementation of CompanyRepository.
 * Uses REST API to interact with the companies table.
 * 
 * Note: Company data is typically read-only for regular users.
 * Only admins (using service role key) can insert/update companies.
 */
public class SupabaseCompanyRepository implements CompanyRepository {
    private final SupabaseClient client;

    public SupabaseCompanyRepository(SupabaseClient client) {
        this.client = client;
    }

    @Override
    public Optional<Company> findByTicker(String ticker) {
        // TODO: Consider implementing caching for frequently accessed company data
        try {
            String encodedTicker = URLEncoder.encode(ticker, "UTF-8");
            Company[] companies = client.queryWithFilter(
                "companies",
                "symbol=eq." + encodedTicker,
                Company[].class
            );

            if (companies != null && companies.length > 0) {
                return Optional.of(companies[0]);
            }
            return Optional.empty();

        } catch (IOException e) {
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while fetching company by ticker: " + ticker, e);
            }
            throw new RepositoryException("Error fetching company by ticker: " + ticker, e);
        }
    }

    @Override
    public Optional<Company> findById(String id) {
        try {
            String encodedId = URLEncoder.encode(id, "UTF-8");
            Company[] companies = client.queryWithFilter(
                "companies",
                "symbol=eq." + encodedId,
                Company[].class
            );

            if (companies != null && companies.length > 0) {
                return Optional.of(companies[0]);
            }
            return Optional.empty();

        } catch (IOException e) {
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while fetching company by ID: " + id, e);
            }
            throw new RepositoryException("Error fetching company by ID: " + id, e);
        }
    }

    @Override
    public List<Company> findBySector(String sector) {
        try {
            Company[] companies = client.queryWithFilter(
                "companies",
                "sector=eq." + sector,
                Company[].class
            );

            return companies != null ? Arrays.asList(companies) : Collections.emptyList();

        } catch (IOException e) {
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while fetching companies by sector", e);
            }
            throw new RepositoryException("Error fetching companies by sector: " + sector, e);
        }
    }

    @Override
    public Company save(Company company) {
        try {
            if (company.getSymbol() == null || company.getSymbol().isEmpty()) {
                throw new DataValidationException("symbol", "Company symbol cannot be null or empty");
            }
            
            Optional<Company> existing = findByTicker(company.getSymbol());
            if (existing.isPresent()) {
                return update(company);
            }
            
            return insert(company);

        } catch (IOException e) {
            if (e.getMessage().contains("permission") || e.getMessage().contains("denied")) {
                throw new PermissionDeniedException("INSERT", "companies");
            }
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while saving company", e);
            }
            throw new RepositoryException("Error saving company: " + company.getSymbol(), e);
        }
    }

    private Company insert(Company company) throws IOException {
        // Note: This may fail if user doesn't have INSERT permission
        // Only service role or admin should insert companies
        // TODO: Handle duplicate key violations and return meaningful error
        Company[] result = client.insert(
            "companies",
            company,
            Company[].class
        );

        if (result != null && result.length > 0) {
            return result[0];
        }
        throw new RepositoryException("Insert failed: no data returned from database");
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
        throw new EntityNotFoundException("Company", company.getSymbol());
    }

    @Override
    public void saveAll(List<Company> companies) {
        // TODO: Optimize with bulk insert instead of one-by-one
        for (Company company : companies) {
            save(company);
        }
    }
}
