package interfaceadapter.view_model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * View model for portfolio display.
 * Contains formatted data ready for presentation in the UI.
 */
public class PortfolioViewModel {
    private final double realizedGain;
    private final double unrealizedGain;
    private final double totalGain;
    private final LocalDateTime snapshotTime;

    public PortfolioViewModel(double realizedGain, double unrealizedGain,
                              double totalGain, LocalDateTime snapshotTime) {
        this.realizedGain = realizedGain;
        this.unrealizedGain = unrealizedGain;
        this.totalGain = totalGain;
        this.snapshotTime = snapshotTime;
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

    /**
     * Returns the snapshot time formatted as a string.
     *
     * @return the formatted snapshot time string
     */
    public String getFormattedSnapshotTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return snapshotTime.format(formatter);
    }
}
