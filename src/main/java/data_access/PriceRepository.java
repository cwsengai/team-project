package data_access;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import entity.PricePoint;
import entity.TimeInterval;

/**
 * Repository interface for PricePoint entity persistence.
 * Manages historical and real-time price data.
 */
public interface PriceRepository {
    /**
     * Save a single price point.
     *
     * @param pricePoint the price point to save
     */
    void savePricePoint(PricePoint pricePoint);

    /**
     * Bulk save multiple price points.
     *
     * @param pricePoints the list of price points to save
     */
    void savePricePoints(List<PricePoint> pricePoints);

    /**
     * Get the most recent price for a ticker at a specific interval.
     *
     * @param ticker the stock ticker
     * @param interval the time interval
     * @return Optional containing the latest price if found, empty otherwise
     */
    Optional<PricePoint> getLatestPrice(String ticker, TimeInterval interval);

    /**
     * Get the latest prices for multiple tickers.
     *
     * @param tickers list of stock tickers
     * @return map of ticker to latest price point
     */
    Map<String, PricePoint> getLatestPrices(List<String> tickers);

    /**
     * Get historical prices for a ticker within a date range.
     *
     * @param ticker the stock ticker
     * @param start start date/time
     * @param end end date/time
     * @param interval the time interval
     * @return list of price points ordered by timestamp
     */
    List<PricePoint> getHistoricalPrices(String ticker, LocalDateTime start, LocalDateTime end, TimeInterval interval);

    /**
     * Delete old price data to save storage.
     *
     * @param olderThan delete price points older than this timestamp
     */
    void cleanup(LocalDateTime olderThan);
}
