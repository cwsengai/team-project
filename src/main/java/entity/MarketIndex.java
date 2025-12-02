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
     * Returns the price formatted as a string with two decimal places.
     *
     * @return the formatted price value
     */
    public String getFormattedPrice() {
        return String.format("%.2f", price);
    }

    /**
     * Returns the percentage change formatted as a string, including a leading
     * plus sign for positive values. The value is formatted to two decimal places
     * followed by a percent symbol.
     *
     * @return the formatted percentage change string with an appropriate sign
     */
    public String getFormattedChangePercent() {
        String sign = changePercent >= 0 ? "+" : "";
        return String.format("%s%.2f%%", sign, changePercent);
    }

    /**
     * Indicates whether the index movement is positive, meaning the percentage
     * change is zero or greater.
     *
     * @return {@code true} if the percentage change is positive or zero;
     *         {@code false} if it is negative
     */
    public boolean isPositive() {
        return changePercent >= 0;
    }
}
