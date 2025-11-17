package use_case.track_portfolio;

/**
 * Input boundary for the Track Portfolio use case.
 * Defines the interface for tracking portfolio performance.
 */
public interface TrackPortfolioInputBoundary {
    /**
     * Execute the track portfolio use case.
     * @param inputData The input data containing portfolio ID and user ID
     */
    void trackPortfolio(TrackPortfolioInputData inputData);
}
