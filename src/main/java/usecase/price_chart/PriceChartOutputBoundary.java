package usecase.price_chart;

import java.util.List;

import entity.PricePoint;
import entity.TimeInterval;

/**
 * Output boundary for presenting price chart data.
 * Formats and delivers price history or error messages to the UI layer.
 */
public interface PriceChartOutputBoundary {

    /**
     * Presents the retrieved price history for a given ticker and interval.
     *
     * @param priceData the list of price points retrieved
     * @param ticker the stock ticker symbol
     * @param interval the selected time interval
     */
    void presentPriceHistory(List<PricePoint> priceData, String ticker, TimeInterval interval);

    /**
     * Presents an error message when price data cannot be retrieved.
     *
     * @param message the error message to display
     */
    void presentError(String message);
}
