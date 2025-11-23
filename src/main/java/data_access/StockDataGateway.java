package data_access;

import java.time.LocalDate;
import java.util.Map;

import entity.PricePoint;

/**
 * Gateway interface for accessing external stock market data.
 * This interface is independent and does not extend PriceProvider to avoid LSP violations.
 * Use cases should depend on StockDataGateway for comprehensive stock data access.
 */
public interface StockDataGateway {
    /**
     * Get the latest prices for multiple stock tickers.
     * @param tickers Array of stock ticker symbols
     * @return Map of ticker to latest price
     */
    Map<String, Double> getLatestPrices(String[] tickers);

    /**
     * Get historical price data for a stock.
     * @param ticker Stock ticker symbol
     * @param start Start date
     * @param end End date
     * @return Array of price points
     */
    PricePoint[] getHistoricalPrices(String ticker, LocalDate start, LocalDate end);

    // TODO: Add method to get real-time quote data
    // TODO: Add method to validate ticker symbols
}
