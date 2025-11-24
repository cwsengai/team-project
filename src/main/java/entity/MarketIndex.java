package entity;

/**
 * Represents a major stock market index.
 */
public class MarketIndex {
    private final String symbol;
    private final String name;
    private final double price;
    private final double change;
    private final double changePercent;

    public MarketIndex(String symbol, String name, double price, double change, double changePercent) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.change = change;
        this.changePercent = changePercent;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getChange() {
        return change;
    }

    public double getChangePercent() {
        return changePercent;
    }

    /**
     * Get formatted price string.
     */
    public String getFormattedPrice() {
        if (price >= 1000) {
            return String.format("%.2f", price);
        } else {
            return String.format("%.2f", price);
        }
    }

    /**
     * Get formatted change percent string with +/- sign.
     */
    public String getFormattedChangePercent() {
        String sign = changePercent >= 0 ? "+" : "";
        return String.format("%s%.2f%%", sign, changePercent);
    }

    /**
     * Check if the index is up (positive change).
     */
    public boolean isPositive() {
        return changePercent >= 0;
    }
}