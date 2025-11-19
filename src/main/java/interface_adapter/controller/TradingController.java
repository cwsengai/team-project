package interface_adapter.controller;

import java.time.LocalDate;

import use_case.track_portfolio.TrackPortfolioInputBoundary;
import use_case.track_portfolio.TrackPortfolioInputData;

/**
 * Controller for trading/portfolio operations.
 * Handles user input from the UI and delegates to use cases.
 * Implements PortfolioController interface to provide abstraction for views.
 */
public class TradingController implements PortfolioController {
    private final TrackPortfolioInputBoundary inputBoundary;

    public TradingController(TrackPortfolioInputBoundary inputBoundary) {
        this.inputBoundary = inputBoundary;
    }

    /**
     * Handle request to view portfolio details.
     * @param portfolioId Portfolio ID to view
     * @param userId User ID requesting the portfolio
     */
    @Override
    public void viewPortfolio(String portfolioId, String userId) {
        // TODO: Add input validation
        if (portfolioId == null || portfolioId.isEmpty()) {
            throw new IllegalArgumentException("Portfolio ID cannot be empty");
        }
        
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }

        // Create input data and call use case
        TrackPortfolioInputData inputData = new TrackPortfolioInputData(
                portfolioId, 
                userId
        );
        
        inputBoundary.trackPortfolio(inputData);
    }

    /**
     * Handle request to refresh portfolio with a specific date.
     * @param portfolioId Portfolio ID to refresh
     * @param userId User ID requesting the refresh
     * @param asOfDate Date for portfolio snapshot
     */
    @Override
    public void refreshPortfolio(String portfolioId, String userId, LocalDate asOfDate) {
        // Note: asOfDate parameter currently not used in input data
        TrackPortfolioInputData inputData = new TrackPortfolioInputData(
                portfolioId, 
                userId
        );
        inputBoundary.trackPortfolio(inputData);
    }
}
