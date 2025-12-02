package entity;

import java.time.LocalDateTime;

/**
 * Represents a price data point at a specific time.
 * Contains OHLC (Open, High, Low, Close) data and volume.
 */
public class PricePoint {
    private final LocalDateTime timestamp;
    private final Double open;
    private final Double high;
    private final Double low;
    private final Double close;

    public PricePoint(LocalDateTime timestamp,
                      Double open, Double high, Double low, Double close) {
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Double getOpen() {
        return open;
    }

    public Double getHigh() {
        return high;
    }

    public Double getLow() {
        return low;
    }

    public Double getClose() {
        return close;
    }

    /**
     * Get the price (alias for close price).
     * @return Close price, or 0.0 if not available
     */
    public double getPrice() {
        return close != null ? close : 0.0;
    }
}
