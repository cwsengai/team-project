package api;

import dataaccess.EnvConfig;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;

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

    private String fetch(String url) throws IOException {
        Request req = new Request.Builder().url(url).build();
        try (Response res = client.newCall(req).execute()) {
            ResponseBody body = res.body();
            if (body == null) {
                throw new IOException("Empty response body");
            }
            return body.string();
        }
    }

    public String getOverview(String symbol) throws Exception {
        String url = BASE_URL + "?function=" + FUNC_OVERVIEW +
                "&symbol=" + symbol + "&apikey=" + apiKey;
        return fetch(url);
    }

    public String getFuncTimeSeriesIntraday(String symbol) throws Exception {
        String url = BASE_URL + "?function=" + FUNC_TIME_SERIES_INTRADAY +
                "&symbol=" + symbol + "&interval=5min&apikey=" + apiKey;
        return fetch(url);
    }

    public String getFuncTimeSeriesDailyAdjusted(String symbol) throws Exception {
        String url = BASE_URL + "?function=" + FUNC_TIME_SERIES_DAILY_ADJUSTED +
                "&symbol=" + symbol + "&apikey=" + apiKey;
        return fetch(url);
    }

    public String getFuncTimeSeriesWeeklyAdjusted(String symbol) throws Exception {
        String url = BASE_URL + "?function=" + FUNC_TIME_SERIES_WEEKLY_ADJUSTED +
                "&symbol=" + symbol + "&apikey=" + apiKey;
        return fetch(url);
    }

    public String getFuncIncomeStatement(String symbol) throws Exception {
        String url = BASE_URL + "?function=" + FUNC_INCOME_STATEMENT +
                "&symbol=" + symbol + "&apikey=" + apiKey;
        return fetch(url);
    }

    public String getFuncBalanceSheet(String symbol) throws Exception {
        String url = BASE_URL + "?function=" + FUNC_BALANCE_SHEET +
                "&symbol=" + symbol + "&apikey=" + apiKey;
        return fetch(url);
    }

    public String getFuncCashFlow(String symbol) throws Exception {
        String url = BASE_URL + "?function=" + FUNC_CASH_FLOW +
                "&symbol=" + symbol + "&apikey=" + apiKey;
        return fetch(url);
    }

    public String getFuncNewsSentiment(String symbol) throws Exception {
        String url = BASE_URL + "?function=" + FUNC_NEWS_SENTIMENT +
                "&tickers=" + symbol + "&apikey=" + apiKey;
        return fetch(url);
    }

    // **********************************************
    // Below are added by Keliu for economic indicators
    public String getEconomicIndicator(String function, String interval) throws Exception {
        String url = BASE_URL + "?function=" + function +
                "&interval=" + interval + "&apikey=" + apiKey;
        return fetch(url);
    }

    public String getGlobalQuote(String symbol) throws Exception {
        String url = BASE_URL + "?function=" + FUNC_GLOBAL_QUOTE +
                "&symbol=" + symbol + "&apikey=" + apiKey;
        return fetch(url);
    }

    // Helper methods
    public String getRealGDP() throws Exception {
        return getEconomicIndicator(FUNC_REAL_GDP, "annual");
    }

    public String getFederalFundsRate() throws Exception {
        return getEconomicIndicator(FUNC_FEDERAL_FUNDS_RATE, "monthly");
    }

    public String getCPI() throws Exception {
        return getEconomicIndicator(FUNC_CPI, "monthly");
    }

    public String getInflation() throws Exception {
        return getEconomicIndicator(FUNC_INFLATION, "monthly");
    }

    public String getUnemploymentRate() throws Exception {
        return getEconomicIndicator(FUNC_UNEMPLOYMENT, "monthly");
    }

    public String getTreasuryYield() throws Exception {
        return getEconomicIndicator(FUNC_TREASURY_YIELD, "monthly");
    }


}
