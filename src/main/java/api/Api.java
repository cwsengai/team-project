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

    private static final String FUNC_TIME_SERIES_INTRADAY = "TIME_SERIES_INTRADAY";
    private static final String FUNC_TIME_SERIES_DAILY_ADJUSTED = "TIME_SERIES_DAILY_ADJUSTED";
    private static final String FUNC_TIME_SERIES_WEEKLY_ADJUSTED = "TIME_SERIES_WEEKLY_ADJUSTED";

    private static final String FUNC_INCOME_STATEMENT = "INCOME_STATEMENT";
    private static final String FUNC_BALANCE_SHEET = "BALANCE_SHEET";
    private static final String FUNC_CASH_FLOW = "CASH_FLOW";

    private static final String FUNC_NEWS_SENTIMENT = "NEWS_SENTIMENT";

    // Added by Keliu.
    // Some finals needed for economic indicators
    private static final String FUNC_REAL_GDP = "REAL_GDP";
    private static final String FUNC_FEDERAL_FUNDS_RATE = "FEDERAL_FUNDS_RATE";
    private static final String FUNC_CPI = "CPI";
    private static final String FUNC_INFLATION = "INFLATION";
    private static final String FUNC_UNEMPLOYMENT = "UNEMPLOYMENT";
    private static final String FUNC_TREASURY_YIELD = "TREASURY_YIELD";

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
    public String getOverview(String symbol) throws IOException {

        final String url = BASE_URL + FUNC_LABEL + FUNC_OVERVIEW + SYMBOL_LABEL + symbol + API_LABEL + apiKey;
        return fetch(url);
    }

    /**
     * Retrieves intraday time-series market data.
     *
     * @param symbol the stock ticker symbol
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getFuncTimeSeriesIntraday(String symbol) throws Exception {
        final String url = BASE_URL + FUNC_LABEL + FUNC_TIME_SERIES_INTRADAY + SYMBOL_LABEL + symbol
                + "&interval=5min&apikey=" + apiKey;
        return fetch(url);
    }

    /**
     * Retrieves daily adjusted time-series market data.
     *
     * @param symbol the stock ticker symbol
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getFuncTimeSeriesDailyAdjusted(String symbol) throws Exception {
        final String url = BASE_URL + FUNC_LABEL + FUNC_TIME_SERIES_DAILY_ADJUSTED + SYMBOL_LABEL + symbol
                + API_LABEL + apiKey;
        return fetch(url);
    }

    /**
     * Retrieves weekly adjusted time-series market data.
     *
     * @param symbol the stock ticker symbol
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getFuncTimeSeriesWeeklyAdjusted(String symbol) throws Exception {
        final String url = BASE_URL + FUNC_LABEL + FUNC_TIME_SERIES_WEEKLY_ADJUSTED + SYMBOL_LABEL + symbol
                + API_LABEL + apiKey;
        return fetch(url);
    }

    /**
     * Retrieves the income statement for the given company symbol.
     *
     * @param symbol the stock ticker symbol
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getFuncIncomeStatement(String symbol) throws IOException {
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
    public String getFuncBalanceSheet(String symbol) throws IOException {
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
    public String getFuncCashFlow(String symbol) throws IOException {
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
    public String getFuncNewsSentiment(String symbol) throws IOException {
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
    /**
     * Retrieves real GDP data.
     *
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getRealGdp() throws Exception {
        return getEconomicIndicator(FUNC_REAL_GDP, "annual");
    }

    /**
     * Retrieves the federal funds rate (monthly).
     *
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getFederalFundsRate() throws Exception {
        return getEconomicIndicator(FUNC_FEDERAL_FUNDS_RATE, MONTHLY_LABEL);
    }

    /**
     * Retrieves the Consumer Price Index (CPI).
     *
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getCpi() throws Exception {
        return getEconomicIndicator(FUNC_CPI, MONTHLY_LABEL);
    }

    /**
     * Retrieves inflation data.
     *
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getInflation() throws Exception {
        return getEconomicIndicator(FUNC_INFLATION, MONTHLY_LABEL);
    }

    /**
     * Retrieves unemployment rate data.
     *
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getUnemploymentRate() throws Exception {
        return getEconomicIndicator(FUNC_UNEMPLOYMENT, MONTHLY_LABEL);
    }

    /**
     * Retrieves treasury yield data.
     *
     * @return the JSON response as a string
     * @throws Exception if the request fails
     */
    public String getTreasuryYield() throws Exception {
        return getEconomicIndicator(FUNC_TREASURY_YIELD, MONTHLY_LABEL);
    }

}
