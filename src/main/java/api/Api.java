package api;

import java.io.IOException;

import dataaccess.EnvConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Client for interacting with the Alpha Vantage API.
 * Provides methods for retrieving company data, financial statements,
 * economic indicators, news sentiment, and time-series market data.
 */
public class Api {

    private static final String BASE_URL = "https://www.alphavantage.co/query";
    private static final String FUNC_OVERVIEW = "OVERVIEW";

    private static final String FUNC_INCOME_STATEMENT = "INCOME_STATEMENT";
    private static final String FUNC_BALANCE_SHEET = "BALANCE_SHEET";
    private static final String FUNC_CASH_FLOW = "CASH_FLOW";

    private static final String FUNC_NEWS_SENTIMENT = "NEWS_SENTIMENT";

    private static final String FUNC_GLOBAL_QUOTE = "GLOBAL_QUOTE";
    // end of Keliu's implementation.

    private static final String FUNC_LABEL = "?function=";
    private static final String SYMBOL_LABEL = "&symbol=";
    private static final String API_LABEL = "&apikey=";
    private static final String MONTHLY_LABEL = "monthly";

    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey;

    /**
     * Creates an API client using the API key from environment configuration.
     */
    public Api() {
        this(EnvConfig.getAlphaVantageApiKey());
    }

    /**
     * Creates an API client with a custom API key.
     *
     * @param apiKey the Alpha Vantage API key
     */
    public Api(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Sends an HTTP GET request to the given URL and returns the response body as a string.
     *
     * @param url the full URL to request
     * @return the response body as a string
     * @throws IOException if the network request fails or returns an empty body
     */
    private String fetch(String url) throws IOException {
        final Request req = new Request.Builder().url(url).build();
        try (Response res = client.newCall(req).execute()) {
            final ResponseBody body = res.body();
            if (body == null) {
                throw new IOException("Empty response body");
            }
            return body.string();
        }
    }

    /**
     * Retrieves company overview information for the given symbol.
     *
     * @param symbol the stock ticker symbol
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getOverview(String symbol) throws Exception {

        final String url = BASE_URL + FUNC_LABEL + FUNC_OVERVIEW + SYMBOL_LABEL + symbol + API_LABEL + apiKey;
        return fetch(url);
    }

    /**
     * Retrieves the income statement for the given company symbol.
     *
     * @param symbol the stock ticker symbol
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getFuncIncomeStatement(String symbol) throws Exception {
        final String url = BASE_URL + FUNC_LABEL + FUNC_INCOME_STATEMENT + SYMBOL_LABEL + symbol + API_LABEL + apiKey;
        return fetch(url);
    }

    /**
     * Retrieves the balance sheet for the given company symbol.
     *
     * @param symbol the stock ticker symbol
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getFuncBalanceSheet(String symbol) throws Exception {
        final String url = BASE_URL + FUNC_LABEL + FUNC_BALANCE_SHEET + SYMBOL_LABEL + symbol + API_LABEL + apiKey;
        return fetch(url);
    }

    /**
     * Retrieves the cash flow statement for the given company symbol.
     *
     * @param symbol the stock ticker symbol
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getFuncCashFlow(String symbol) throws Exception {
        final String url = BASE_URL + FUNC_LABEL + FUNC_CASH_FLOW + SYMBOL_LABEL + symbol + API_LABEL + apiKey;
        return fetch(url);
    }

    /**
     * Retrieves news sentiment data for the given symbol.
     *
     * @param symbol the stock ticker symbol
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getFuncNewsSentiment(String symbol) throws Exception {
        final String url = BASE_URL + FUNC_LABEL + FUNC_NEWS_SENTIMENT + "&tickers=" + symbol + API_LABEL + apiKey;
        return fetch(url);
    }

    // **********************************************
    // Below are added by Keliu for economic indicators
    /**
     * Retrieves a general economic indicator from Alpha Vantage.
     *
     * @param function the indicator function name
     * @param interval the time interval (e.g., monthly or annual)
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getEconomicIndicator(String function, String interval) throws Exception {
        final String url = BASE_URL + FUNC_LABEL + function + "&interval=" + interval + API_LABEL + apiKey;
        return fetch(url);
    }

    /**
     * Retrieves the global quote for a given stock symbol.
     *
     * @param symbol the stock ticker symbol
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getGlobalQuote(String symbol) throws Exception {
        final String url = BASE_URL + FUNC_LABEL + FUNC_GLOBAL_QUOTE + SYMBOL_LABEL + symbol + API_LABEL + apiKey;
        return fetch(url);
    }

    // Helper methods

}
