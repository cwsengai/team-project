package usecase.portfolio_statistics;

import java.util.UUID;

public interface PortfolioStatisticsInputBoundary {
    /**
     * Request the calculation of portfolio statistics for the given user.
     * The result should be delivered to the provided output boundary (presenter).
     */
    void requestPortfolioSummary(UUID userId);
}
