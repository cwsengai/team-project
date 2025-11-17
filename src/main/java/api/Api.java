package api;



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

    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey;

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

            String text = body.string().trim();

            if (text.isEmpty()) {
                throw new IOException("Empty response text");
            }

            if (text.equals("{}") || text.equals("{ }")) {
                throw new IOException("AlphaVantage returned empty JSON (invalid symbol)");
            }

            if (text.contains("\"Error Message\"")) {
                throw new IOException("AlphaVantage error: " + text);
            }

            return text;
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


}