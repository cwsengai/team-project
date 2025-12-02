package entity;

import java.time.LocalDateTime;

/**
 * Represents a price data point at a specific time.
 * Contains OHLC (Open, High, Low, Close) data and volume.
 * 
 * @param timestamp the date and time of the price point
 * @param open      the opening price
 * @param high      the highest price
 * @param low       the lowest price
 * @param close     the closing price
 */
public record PricePoint(LocalDateTime timestamp, Double open, Double high, Double low, Double close) {

    /**
     * Get the price (alias for close price).
     *
     * @return Close price, or 0.0 if not available
     */
    public double getPrice() {
        if (close == null) {
            return 0.0;
        }
        return close;
    }
}
