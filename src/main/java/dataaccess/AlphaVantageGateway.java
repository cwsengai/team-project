package dataaccess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import entity.PricePoint;

/**
 * Implementation of StockDataGateway using Alpha Vantage API.
 * Currently returns mock data - TODO: Implement actual API calls.
 */
public class AlphaVantageGateway implements StockDataGateway {
    

    /**
     * Creates an AlphaVantageGateway using the API key from environment configuration.
     */
    public AlphaVantageGateway() {
        this(EnvConfig.getAlphaVantageApiKey());
    }

    /**
     * Creates an AlphaVantageGateway with a custom API key.
     *
     * @param apiKey the Alpha Vantage API key
     */
    public AlphaVantageGateway(String apiKey) {
    }

    @Override
    public Map<String, Double> getLatestPrices(String[] tickers) {
        // TODO: Implement actual API call to Alpha Vantage
        // For now, return mock data
        Map<String, Double> prices = new HashMap<>();
        
        for (String ticker : tickers) {
            // Mock prices - replace with actual API call
            switch (ticker) {
                case "AAPL":
                    prices.put(ticker, 175.50);
                    break;
                case "GOOGL":
                    prices.put(ticker, 140.25);
                    break;
                case "MSFT":
                    prices.put(ticker, 378.90);
                    break;
                default:
                    prices.put(ticker, 100.00);
            }
        }
        
        return prices;
    }

    @Override
    public PricePoint[] getHistoricalPrices(String ticker, LocalDate start, LocalDate end) {
        // TODO: Implement actual API call to Alpha Vantage TIME_SERIES_DAILY
        // TODO: Parse JSON response and create PricePoint array
        // TODO: Handle API rate limits
        // TODO: Implement error handling for invalid tickers
        
        // Mock data for now
        return new PricePoint[] {
            new PricePoint(LocalDateTime.now().minusDays(2), 100.00),
            new PricePoint(LocalDateTime.now().minusDays(1), 102.50),
            new PricePoint(LocalDateTime.now(), 105.00)
        };
    }

    /**
     * TODO: Implement method to make HTTP request to Alpha Vantage API
     */
    private String makeApiRequest(String endpoint) {
        // Use HttpClient or similar to make request
        return "";
    }

    /**
     * TODO: Implement JSON parsing for API response
     */
    private Map<String, Double> parseQuoteResponse(String jsonResponse) {
        // Parse JSON and extract prices
        return new HashMap<>();
    }
}
