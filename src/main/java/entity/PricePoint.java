package entity;

import java.time.LocalDateTime;

/**
 * Represents a price data point at a specific time.
 * Contains OHLC (Open, High, Low, Close) data and volume.
 */
public record PricePoint(LocalDateTime timestamp, Double open, Double high, Double low, Double close) {

    /**
     * Get the price (alias for close price).
     *
     * @return Close price, or 0.0 if not available
     */
    public double getPrice() {
        return close != null ? close : 0.0;
    }
}
