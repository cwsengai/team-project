package use_case.track_portfolio;

import java.time.LocalDateTime;
import java.util.Map;

import data_access.PortfolioRepository;
import data_access.StockDataGateway;
import entity.Portfolio;
import entity.Position;

/**
 * Interactor for the TrackPortfolio use case.
 * Implements the business logic for retrieving and computing portfolio data.
 */
public class TrackPortfolioInteractor implements TrackPortfolioInputBoundary {
    private final PortfolioRepository portfolioRepository;
    private final StockDataGateway stockDataGateway;
    private final TrackPortfolioOutputBoundary outputBoundary;

    public TrackPortfolioInteractor(PortfolioRepository portfolioRepository,
                                    StockDataGateway stockDataGateway,
                                    TrackPortfolioOutputBoundary outputBoundary) {
        this.portfolioRepository = portfolioRepository;
        this.stockDataGateway = stockDataGateway;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void trackPortfolio(TrackPortfolioInputData inputData) {
        try {
            // Retrieve portfolio from repository
            Portfolio portfolio = portfolioRepository.findById(inputData.getPortfolioId())
                    .orElse(null);
            
            if (portfolio == null) {
                outputBoundary.presentError("Portfolio not found: " + inputData.getPortfolioId());
                return;
            }

            // TODO: Verify that the portfolio belongs to the user
            if (!portfolio.getUserId().equals(inputData.getUserId())) {
                outputBoundary.presentError("Unauthorized access to portfolio");
                return;
            }

            // Get current market prices for all positions
            String[] tickers = portfolio.getPositions().stream()
                    .map(Position::getTicker)
                    .toArray(String[]::new);
            
            Map<String, Double> currentPrices = stockDataGateway.getLatestPrices(tickers);

            // Compute gains
            TrackPortfolioOutputData outputData = computeGains(portfolio, currentPrices);

            // Present results
            outputBoundary.presentPortfolio(outputData);

        } catch (Exception e) {
            // TODO: Implement proper error handling and logging
            outputBoundary.presentError("Error tracking portfolio: " + e.getMessage());
        }
    }

    /**
     * Compute realized and unrealized gains for the portfolio.
     * Uses the portfolio entity's business logic with fetched price data.
     */
    private TrackPortfolioOutputData computeGains(Portfolio portfolio, 
                                                   Map<String, Double> currentPrices) {
        // Calculate total realized gains (independent of current prices)
        double totalRealizedGain = portfolio.calculateRealizedGains();

        // Calculate total unrealized gains using portfolio's business logic
        double totalUnrealizedGain = portfolio.calculateUnrealizedGains(currentPrices);

        // Create output data
        Position[] positions = portfolio.getPositions().toArray(new Position[0]);
        
        return new TrackPortfolioOutputData(
                portfolio.getId(),
                positions,
                totalRealizedGain,
                totalUnrealizedGain,
                LocalDateTime.now()
        );
    }

    /**
     * TODO: Add method to handle historical portfolio snapshots
     * TODO: Add method to calculate portfolio performance over time
     * TODO: Add caching for stock prices to reduce API calls
     */
}
