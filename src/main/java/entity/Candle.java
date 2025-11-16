package entity;

import java.time.LocalDateTime;

/**
 * Represents an aggregated OHLC (Open, High, Low, Close) candle for charting.
 * Candles are typically aggregated from price points over a time interval.
 */
public class Candle {
    private final String id;
    private final String companyId;
    private final TimeInterval interval;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final double open;
    private final double high;
    private final double low;
    private final double close;
    private final Double volume;
    private final LocalDateTime createdAt;

    public Candle(String id, String companyId, TimeInterval interval,
                  LocalDateTime startTime, LocalDateTime endTime,
                  double open, double high, double low, double close,
                  Double volume, LocalDateTime createdAt) {
        this.id = id;
        this.companyId = companyId;
        this.interval = interval;
        this.startTime = startTime;
        this.endTime = endTime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getCompanyId() {
        return companyId;
    }

    public TimeInterval getInterval() {
        return interval;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public Double getVolume() {
        return volume;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Calculate the body size of the candle.
     * @return Absolute difference between open and close
     */
    public double getBodySize() {
        return Math.abs(close - open);
    }

    /**
     * Calculate the full range of the candle.
     * @return Difference between high and low
     */
    public double getRange() {
        return high - low;
    }

    /**
     * Check if this is a bullish candle (close > open).
     * @return true if bullish, false otherwise
     */
    public boolean isBullish() {
        return close > open;
    }
}
