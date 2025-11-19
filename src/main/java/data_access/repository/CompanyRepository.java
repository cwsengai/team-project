package data_access.repository;

import java.util.List;
import java.util.Optional;

import entity.Company;

/**
 * Repository interface for Company entity persistence.
 * Manages company/stock metadata.
 */
public interface CompanyRepository {
    Optional<Company> findByTicker(String ticker);

    List<Company> findBySector(String sector);

    Company save(Company company);

    void saveAll(List<Company> companies);
}
