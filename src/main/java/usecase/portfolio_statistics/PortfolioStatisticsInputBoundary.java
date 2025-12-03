package usecase.portfolio_statistics;

import java.util.UUID;

/**
 * Input boundary for the Portfolio Statistics use case.
 * Allows clients to request the calculation of portfolio summary statistics.
 */
public interface PortfolioStatisticsInputBoundary {

    /**
     * Requests the calculation of portfolio statistics for the specified user.
     *
     * @param userId the unique identifier of the user whose statistics are requested
     */
    void requestPortfolioSummary(UUID userId);
}
