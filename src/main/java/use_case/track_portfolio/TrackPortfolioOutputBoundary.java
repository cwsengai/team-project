package use_case.track_portfolio;

/**
 * Output boundary for the Track Portfolio use case.
 * Defines the interface for presenting portfolio tracking results.
 */
public interface TrackPortfolioOutputBoundary {
    /**
     * Present the portfolio data.
     * @param outputData The output data containing portfolio performance metrics
     */
    void presentPortfolio(TrackPortfolioOutputData outputData);

    /**
     * Present an error message.
     * @param error The error message to present
     */
    void presentError(String error);
}
