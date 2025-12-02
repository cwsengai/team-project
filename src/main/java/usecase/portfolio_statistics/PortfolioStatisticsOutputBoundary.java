package usecase.portfolio_statistics;

/**
 * Output boundary for the Portfolio Statistics use case.
 * Defines how calculated statistics should be presented to the user interface layer.
 */
public interface PortfolioStatisticsOutputBoundary {

    /**
     * Presents the calculated portfolio statistics.
     * Implementations (presenters) should format the data
     * and forward it to the UI layer.
     *
     * @param outputData the calculated portfolio statistics
     */
    void present(PortfolioStatisticsOutputData outputData);
}
