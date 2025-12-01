package usecase.portfolio_statistics;

public interface PortfolioStatisticsOutputBoundary {
    /**
     * Present the calculated portfolio statistics. Implementations (presenters)
     * should format the data and make it available to the UI layer.
     */
    void present(PortfolioStatisticsOutputData outputData);
}
