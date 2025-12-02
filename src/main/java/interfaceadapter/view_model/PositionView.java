package interfaceadapter.view_model;

/**
 * View model for a single position in the portfolio.
 * Contains formatted data ready for presentation in the UI.
 * 
 * @param marketValue the current market value of the position
 * @param gain        the gain or loss of the position
 */
public record PositionView(double marketValue, double gain) {

    public String getFormattedMarketValue() {
        return String.format("$%.2f", marketValue);
    }

    public String getFormattedGain() {
        return String.format("$%.2f", gain);
    }
}
