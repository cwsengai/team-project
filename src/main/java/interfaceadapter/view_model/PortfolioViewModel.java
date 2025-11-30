package interfaceadapter.view_model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * View model for portfolio display.
 * Contains formatted data ready for presentation in the UI.
 */
public class PortfolioViewModel {
    private final String portfolioId;
    private final PositionView[] positions;
    private final double realizedGain;
    private final double unrealizedGain;
    private final double totalGain;
    private final LocalDateTime snapshotTime;

    public PortfolioViewModel(String portfolioId, PositionView[] positions,
                            double realizedGain, double unrealizedGain,
                            double totalGain, LocalDateTime snapshotTime) {
        this.portfolioId = portfolioId;
        this.positions = positions;
        this.realizedGain = realizedGain;
        this.unrealizedGain = unrealizedGain;
        this.totalGain = totalGain;
        this.snapshotTime = snapshotTime;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public PositionView[] getPositions() {
        return positions;
    }

    public double getRealizedGain() {
        return realizedGain;
    }

    public double getUnrealizedGain() {
        return unrealizedGain;
    }

    public double getTotalGain() {
        return totalGain;
    }

    public LocalDateTime getSnapshotTime() {
        return snapshotTime;
    }

    public String getFormattedRealizedGain() {
        return String.format("$%.2f", realizedGain);
    }

    public String getFormattedUnrealizedGain() {
        return String.format("$%.2f", unrealizedGain);
    }

    public String getFormattedTotalGain() {
        return String.format("$%.2f", totalGain);
    }

    public String getFormattedSnapshotTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return snapshotTime.format(formatter);
    }
}
