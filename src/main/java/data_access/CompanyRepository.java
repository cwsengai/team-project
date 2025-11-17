package data_access;

import java.util.List;
import java.util.Optional;

import entity.Company;

/**
 * Repository interface for Company entity persistence.
 * Manages company/stock metadata.
 */
public interface CompanyRepository {
    /**
     * Find a company by its ticker symbol.
     *
     * @param ticker the stock ticker (e.g., "AAPL")
     * @return Optional containing the company if found, empty otherwise
     */
    Optional<Company> findByTicker(String ticker);

    /**
     * Find a company by its unique ID.
     *
     * @param id the company ID (UUID)
     * @return Optional containing the company if found, empty otherwise
     */
    Optional<Company> findById(String id);

    /**
     * Find all companies in a specific sector.
     *
     * @param sector the sector name (e.g., "Technology")
     * @return list of companies in the sector
     */
    List<Company> findBySector(String sector);

    /**
     * Save or update a company.
     *
     * @param company the company to save
     * @return the saved company with generated ID if new
     */
    Company save(Company company);

    /**
     * Bulk save multiple companies.
     *
     * @param companies the list of companies to save
     */
    void saveAll(List<Company> companies);
}
