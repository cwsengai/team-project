package dataaccess;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import api.Api;
import entity.MarketIndex;
import usecase.MarketIndexGateway;

/**
 * Implementation of MarketIndexGateway using Alpha Vantage API.
 * Uses ETFs that track major indices:
 * - SPY: Tracks S&P 500
 * - QQQ: Tracks NASDAQ-100
 * - DIA: Tracks Dow Jones Industrial Average
 */
public class AlphaVantageMarketIndexGateway implements MarketIndexGateway {
    private final Api api;

    // ETF symbols that track major indices
    private static final String SP500_SYMBOL = "SPY";
    private static final String NASDAQ_SYMBOL = "QQQ";
    private static final String DOW_SYMBOL = "DIA";

    public AlphaVantageMarketIndexGateway(Api api) {
        this.api = api;
    }

    @Override
    public List<MarketIndex> getMarketIndices() throws Exception {
        List<MarketIndex> indices = new ArrayList<>();

        try {
            // Fetch S&P 500
            indices.add(getMarketIndex(SP500_SYMBOL));
            Thread.sleep(12000);

            // Fetch NASDAQ
            indices.add(getMarketIndex(NASDAQ_SYMBOL));
            Thread.sleep(12000);

            // Fetch Dow Jones
            indices.add(getMarketIndex(DOW_SYMBOL));

        }
        catch (Exception ex) {
            System.err.println("Error fetching market indices: " + ex.getMessage());
            // Return whatever we managed to fetch, or dummy data
            if (indices.isEmpty()) {
                indices = getDummyIndices();
            }
        }

        return indices;
    }

    @Override
    public MarketIndex getMarketIndex(String symbol) throws Exception {
        try {
            String jsonResponse = api.getGlobalQuote(symbol);

            if (jsonResponse == null || jsonResponse.isEmpty()) {
                System.err.println("Empty response for " + symbol);
                return createDummyIndex(symbol);
            }

            JSONObject json = new JSONObject(jsonResponse);

            // Check for API errors
            if (json.has("Note")) {
                System.err.println("API rate limit for " + symbol + ": " + json.getString("Note"));
                return createDummyIndex(symbol);
            }

            if (json.has("Error Message")) {
                System.err.println("API error for " + symbol + ": " + json.getString("Error Message"));
                return createDummyIndex(symbol);
            }

            // Parse Global Quote response
            if (json.has("Global Quote")) {
                JSONObject quote = json.getJSONObject("Global Quote");

                // Check if quote is empty
                if (quote.length() == 0) {
                    System.err.println("Empty quote data for " + symbol);
                    return createDummyIndex(symbol);
                }

                String indexSymbol = quote.optString("01. symbol", symbol);
                double price = quote.optDouble("05. price", 0.0);
                final double change = quote.optDouble("09. change", 0.0);
                String changePercentStr = quote.optString("10. change percent", "0%");

                // Parse change percent (remove % sign)
                double changePercent = 0.0;
                try {
                    changePercent = Double.parseDouble(changePercentStr.replace("%", "").trim());
                }
                catch (NumberFormatException ex) {
                    System.err.println("Could not parse change percent for " + symbol + ": " + changePercentStr);
                    changePercent = 0.0;
                }

                // Validate data
                if (price == 0.0) {
                    System.err.println("Warning: Zero price for " + symbol + ", using dummy data");
                    return createDummyIndex(symbol);
                }

                String name = getIndexName(indexSymbol);

                System.out.println("✅ Successfully fetched " + name + ": $" + price + " (" + changePercent + "%)");

                return new MarketIndex(indexSymbol, name, price, change, changePercent);
            }

            System.err.println("No 'Global Quote' in response for " + symbol);
            return createDummyIndex(symbol);

        }
        catch (Exception ex) {
            System.err.println("Error fetching " + symbol + ": " + ex.getMessage());
            return createDummyIndex(symbol);
        }
    }

    private String getIndexName(String symbol) {
        switch (symbol.toUpperCase()) {
            case "SPY":
                return "S&P 500";
            case "QQQ":
                return "NASDAQ";
            case "DIA":
                return "Dow Jones";
            default:
                return symbol;
        }
    }

    private MarketIndex createDummyIndex(String symbol) {
        String name = getIndexName(symbol);

        // Return realistic dummy data based on actual ranges
        switch (symbol.toUpperCase()) {
            case "SPY":
                return new MarketIndex("SPY", "S&P 500", 579.32, 0.75, 0.13);
            case "QQQ":
                return new MarketIndex("QQQ", "NASDAQ", 520.15, -1.09, -0.21);
            case "DIA":
                return new MarketIndex("DIA", "Dow Jones", 438.56, 0.70, 0.16);
            default:
                return new MarketIndex(symbol, name, 0.0, 0.0, 0.0);
        }
    }

    /**
     * Returns a predefined list of major market indices used as fallback data
     * when external API requests fail. These static placeholder values allow
     * the application to continue displaying meaningful market information
     * even when real-time data cannot be retrieved.
     *
     * @return a list of fallback {@link MarketIndex} objects
     */
    private List<MarketIndex> getDummyIndices() {
        List<MarketIndex> dummyList = new ArrayList<>();
        dummyList.add(new MarketIndex("SPY", "S&P 500", 579.32, 0.75, 0.13));
        dummyList.add(new MarketIndex("QQQ", "NASDAQ", 520.15, -1.09, -0.21));
        dummyList.add(new MarketIndex("DIA", "Dow Jones", 438.56, 0.70, 0.16));
        return dummyList;
    }
}
