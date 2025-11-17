package use_case.track_portfolio;

import java.time.LocalDateTime;

import entity.Position;

/**
 * Output data for the Track Portfolio use case.
 */
public class TrackPortfolioOutputData {
    private final String portfolioId;
    private final Position[] positions;
    private final double realizedGains;
    private final double unrealizedGains;
    private final double totalValue;
    private final LocalDateTime snapshotTime;

    public TrackPortfolioOutputData(String portfolioId, Position[] positions,
                                   double realizedGains, double unrealizedGains,
                                   double totalValue, LocalDateTime snapshotTime) {
        this.portfolioId = portfolioId;
        this.positions = positions;
        this.realizedGains = realizedGains;
        this.unrealizedGains = unrealizedGains;
        this.totalValue = totalValue;
        this.snapshotTime = snapshotTime;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public Position[] getPositions() {
        return positions;
    }

    public double getRealizedGains() {
        return realizedGains;
    }

    public double getUnrealizedGains() {
        return unrealizedGains;
    }

    // Aliases for consistency with different naming conventions
    public double getTotalRealizedGain() {
        return realizedGains;
    }

    public double getTotalUnrealizedGain() {
        return unrealizedGains;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public LocalDateTime getSnapshotTime() {
        return snapshotTime;
    }
}
