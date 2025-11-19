package data_access.repository;

import java.util.List;
import java.util.Optional;

import entity.Position;

/**
 * Repository interface for Position entity persistence.
 * Manages stock positions within portfolios.
 */
public interface PositionRepository {
    /**
     * Find all positions in a portfolio.
     *
     * @param portfolioId the portfolio ID
     * @return list of positions
     */
    List<Position> findByPortfolioId(String portfolioId);

    /**
     * Find a specific position by portfolio and ticker.
     *
     * @param portfolioId the portfolio ID
     * @param ticker the stock ticker
     * @return Optional containing the position if found, empty otherwise
     */
    Optional<Position> findByPortfolioAndTicker(String portfolioId, String ticker);

    /**
     * Find a specific position by portfolio and company ID.
     *
     * @param portfolioId the portfolio ID
     * @param companyId the company ID
     * @return Optional containing the position if found, empty otherwise
     */
    Optional<Position> findByPortfolioAndCompany(String portfolioId, String companyId);

    /**
     * Save or update a position.
     *
     * @param position the position to save
     * @return the saved position with generated ID if new
     */
    Position save(Position position);

    /**
     * Update the profit/loss values for a position.
     *
     * @param positionId the position ID
     * @param realizedPL realized profit/loss
     * @param unrealizedPL unrealized profit/loss
     */
    void updatePL(String positionId, double realizedPL, double unrealizedPL);
}
