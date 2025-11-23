package use_case.track_portfolio;

/**
 * Input data for the Track Portfolio use case.
 */
public class TrackPortfolioInputData {
    private final String portfolioId;
    private final String userId;

    public TrackPortfolioInputData(String portfolioId, String userId) {
        this.portfolioId = portfolioId;
        this.userId = userId;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public String getUserId() {
        return userId;
    }
}
