package usecase.price_chart;

import entity.TimeInterval;

/**
 * Input boundary for requesting price history based on a given time interval.
 */
public interface PriceInputBoundary {

    /**
     * Loads the price history for a specified ticker and time interval.
     *
     * @param ticker the stock ticker symbol
     * @param interval the selected time interval for price retrieval
     */
    void loadPriceHistory(String ticker, TimeInterval interval);
}
