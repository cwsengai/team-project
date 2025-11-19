package data_access.repository;

import java.time.LocalDateTime;
import java.util.List;

import entity.Trade;

/**
 * Repository interface for Trade entity persistence.
 * Manages buy/sell transaction records.
 */
public interface TradeRepository {
    /**
     * Save a new trade.
     * Trades are immutable once created.
     *
     * @param trade the trade to save
     * @return the saved trade with generated ID
     */
    Trade save(Trade trade);

    /**
     * Find all trades for a portfolio.
     *
     * @param portfolioId the portfolio ID
     * @return list of trades ordered by execution time (newest first)
     */
    List<Trade> findByPortfolioId(String portfolioId);

    /**
     * Find all trades for a specific position.
     *
     * @param positionId the position ID
     * @return list of trades ordered by execution time
     */
    List<Trade> findByPositionId(String positionId);

    /**
     * Find trades within a date range for a portfolio.
     *
     * @param portfolioId the portfolio ID
     * @param start start date/time
     * @param end end date/time
     * @return list of trades in the date range
     */
    List<Trade> findByPortfolioInDateRange(String portfolioId, LocalDateTime start, LocalDateTime end);
}
