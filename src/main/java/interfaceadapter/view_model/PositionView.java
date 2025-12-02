package interfaceadapter.view_model;

/**
 * View model for a single position in the portfolio.
 * Contains formatted data ready for presentation in the UI.
 */
public record PositionView(double marketValue, double gain) {

    public String getFormattedMarketValue() {
        return String.format("$%.2f", marketValue);
    }

    public String getFormattedGain() {
        return String.format("$%.2f", gain);
    }
}
