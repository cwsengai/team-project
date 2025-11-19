package data_access.repository;

import java.util.List;
import java.util.Optional;

import entity.Portfolio;

/**
 * Repository interface for Portfolio persistence.
 * Manages portfolio CRUD operations.
 */
public interface PortfolioRepository {
    Optional<Portfolio> findById(String id);

    List<Portfolio> findByUserId(String userId);

    Portfolio save(Portfolio portfolio);

    void updateCash(String id, double cash);

    void delete(String id);
}
