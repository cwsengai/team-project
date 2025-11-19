package entity;

import java.time.LocalDateTime;

/**
 * Represents a price data point at a specific time.
 * Contains OHLC (Open, High, Low, Close) data and volume.
 */
public class PricePoint {
    private final String companySymbol;  // Matches DB: company_symbol (references companies.symbol)
    private final LocalDateTime timestamp;
    private final TimeInterval interval;
    private final Double open;
    private final Double high;
    private final Double low;
    private final Double close;
    private final Double volume;
    private final String source;

    public PricePoint(String companySymbol, LocalDateTime timestamp, TimeInterval interval,
                      Double open, Double high, Double low, Double close, Double volume, String source) {
        this.companySymbol = companySymbol;
        this.timestamp = timestamp;
        this.interval = interval;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.source = source;
    }

    // Simplified constructor for basic price data
    public PricePoint(LocalDateTime timestamp, double price) {
        this(null, timestamp, TimeInterval.DAILY, null, null, null, price, null, null);
    }

    public String getCompanySymbol() {
        return companySymbol;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TimeInterval getInterval() {
        return interval;
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

    public Double getVolume() {
        return volume;
    }

    public String getSource() {
        return source;
    }

    /**
     * Get the price (alias for close price).
     * @return Close price, or 0.0 if not available
     */
    public double getPrice() {
        if (close == null) {
            return 0.0;
        }
        return close;
    }
}
