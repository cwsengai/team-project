package usecase.price_chart;

import java.util.List;

import entity.PricePoint;
import entity.TimeInterval;

/**
 * Data access interface for retrieving historical price data.
 */
public interface PriceDataAccessInterface {

    /**
     * Returns the price history for the given ticker and time interval.
     *
     * @param ticker the stock ticker symbol
     * @param interval the selected time interval
     * @return a list of price points representing historical prices
     * @throws Exception if price data cannot be retrieved
     */
    List<PricePoint> getPriceHistory(String ticker, TimeInterval interval) throws Exception;
}
