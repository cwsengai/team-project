package interface_adapter.controller;

import java.time.LocalDate;

/**
 * Interface for portfolio controller operations.
 * Defines the contract for handling user actions related to portfolio management.
 * This abstraction allows the view to be decoupled from concrete controller implementations.
 */
public interface PortfolioController {
    /**
     * Handle request to view portfolio details.
     * @param portfolioId Portfolio ID to view
     * @param userId User ID requesting the portfolio
     */
    void viewPortfolio(String portfolioId, String userId);

    /**
     * Handle request to refresh portfolio with a specific date.
     * @param portfolioId Portfolio ID to refresh
     * @param userId User ID requesting the refresh
     * @param asOfDate Date for portfolio snapshot
     */
    void refreshPortfolio(String portfolioId, String userId, LocalDate asOfDate);
}
