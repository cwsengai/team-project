package data_access.repository;

import java.time.LocalDateTime;
import java.util.List;

import entity.Trade;

/**
 * Repository interface for Trade entity persistence.
 * Manages buy/sell transaction records.
 */
public interface TradeRepository {
    Trade save(Trade trade);

    List<Trade> findByPortfolioId(String portfolioId);

    List<Trade> findByPositionId(String positionId);

    List<Trade> findByPortfolioInDateRange(String portfolioId, LocalDateTime start, LocalDateTime end);

    void delete(String id);
}
