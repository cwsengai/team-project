package data_access.gateway;

import java.time.LocalDate;
import java.util.Map;

import entity.PricePoint;

/**
 * Gateway interface for accessing external stock market data.
 * This interface is independent and does not extend PriceProvider to avoid LSP violations.
 * Use cases should depend on StockDataGateway for comprehensive stock data access.
 */
public interface StockDataGateway {
    Map<String, Double> getLatestPrices(String[] tickers);

    PricePoint[] getHistoricalPrices(String ticker, LocalDate start, LocalDate end);

    // TODO: Add method to get real-time quote data
    // TODO: Add method to validate ticker symbols
}
