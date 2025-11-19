package data_access.repository;

import java.util.List;
import java.util.Optional;

import entity.Position;

/**
 * Repository interface for Position entity persistence.
 * Manages stock positions within portfolios.
 */
public interface PositionRepository {
    List<Position> findByPortfolioId(String portfolioId);

    Optional<Position> findByPortfolioAndTicker(String portfolioId, String ticker);

    Optional<Position> findByPortfolioAndCompany(String portfolioId, String companyId);

    Position save(Position position);

    void updatePL(String positionId, double realizedPL, double unrealizedPL);

    void delete(String id);
}
