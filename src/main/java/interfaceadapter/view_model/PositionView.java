package interfaceadapter.view_model;

/**
 * View model for a single position in the portfolio.
 * Contains formatted data ready for presentation in the UI.
 */
public class PositionView {
    private final double marketValue;
    private final double gain;

    public PositionView(double marketValue, double gain) {
        this.marketValue = marketValue;
        this.gain = gain;
    }

    public String getFormattedMarketValue() {
        return String.format("$%.2f", marketValue);
    }

    public String getFormattedGain() {
        return String.format("$%.2f", gain);
    }
}
