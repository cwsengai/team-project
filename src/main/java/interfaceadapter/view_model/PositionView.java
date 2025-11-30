package interfaceadapter.view_model;

/**
 * View model for a single position in the portfolio.
 * Contains formatted data ready for presentation in the UI.
 */
public class PositionView {
    private final String ticker;
    private final int quantity;
    private final double averageCost;
    private final double marketPrice;
    private final double marketValue;
    private final double gain;

    public PositionView(String ticker, int quantity, double averageCost,
                       double marketPrice, double marketValue, double gain) {
        this.ticker = ticker;
        this.quantity = quantity;
        this.averageCost = averageCost;
        this.marketPrice = marketPrice;
        this.marketValue = marketValue;
        this.gain = gain;
    }

    public String getTicker() {
        return ticker;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getAverageCost() {
        return averageCost;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public double getMarketValue() {
        return marketValue;
    }

    public double getGain() {
        return gain;
    }

    public String getFormattedMarketValue() {
        return String.format("$%.2f", marketValue);
    }

    public String getFormattedGain() {
        return String.format("$%.2f", gain);
    }
}
