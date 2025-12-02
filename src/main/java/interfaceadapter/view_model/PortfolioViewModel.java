package interfaceadapter.view_model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * View model for portfolio display.
 * Contains formatted data ready for presentation in the UI.
 * 
 * @param realizedGain   the realized gain of the portfolio
 * @param unrealizedGain the unrealized gain of the portfolio
 * @param totalGain      the total gain of the portfolio
 * @param snapshotTime   the time the portfolio snapshot was taken
 */
public record PortfolioViewModel(double realizedGain, double unrealizedGain, double totalGain,
                                 LocalDateTime snapshotTime) {

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
