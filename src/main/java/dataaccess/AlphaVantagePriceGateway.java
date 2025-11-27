package dataaccess;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import entity.PricePoint;
import entity.TimeInterval;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import use_case.price_chart.PriceDataAccessInterface;

/**
 * Implementation of the PriceDataAccessInterface using the AlphaVantage API.
 */
public class AlphaVantagePriceGateway implements PriceDataAccessInterface {

    private static final String API_KEY = "3TEXXG3G3UXFI7E2";
    private static final String BASE_URL = "https://www.alphavantage.co/query?";
    private static final String NOTE_KEY = "Note";
    private static final String ERROR_MESSAGE_KEY = "Error Message";
    private static final String INTRADAY_INTERVAL = "5min";
    private static final String TIME_SERIES_INTRADAY = "TIME_SERIES_INTRADAY";
    private static final String TIME_SERIES_DAILY = "TIME_SERIES_DAILY";
    private static final String TIME_SERIES_WEEKLY = "TIME_SERIES_WEEKLY";
    private static final String TIME_SERIES_5MIN = "Time Series (5min)";
    private static final String TIME_SERIES_DAILY_KEY = "Time Series (Daily)";
    private static final String TIME_SERIES_WEEKLY_KEY = "Weekly Time Series";
    private static final String TIME_SERIES_MONTHLY_KEY = "Monthly Time Series";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String MIDNIGHT_TIME = " 00:00:00";
    
    private final OkHttpClient httpClient = new OkHttpClient();

    private String sendHttpRequest(String urlString) throws IOException {
        final Request request = new Request.Builder()
                .url(urlString)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API request failed with code: " + response.code());
            }
            final ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("API response body is null");
            }
            return body.string();
        }
    }

    @Override
    public List<PricePoint> getPriceHistory(String ticker, TimeInterval interval) throws IOException {
        final String functionName = getFunctionName(interval);
        final String url = buildUrl(functionName, ticker, interval);
        final String jsonResponse = sendHttpRequest(url);
        return parseJsonToPricePoints(jsonResponse, interval);
    }

    private String buildUrl(String functionName, String ticker, TimeInterval interval) {
        final StringBuilder urlBuilder = new StringBuilder(BASE_URL);
        urlBuilder.append("function=").append(functionName)
                .append("&symbol=").append(ticker)
                .append("&apikey=").append(API_KEY);

        if (interval == TimeInterval.FIVE_MINUTES) {
            urlBuilder.append("&interval=").append(INTRADAY_INTERVAL);
        }
        return urlBuilder.toString();
    }

    private String getFunctionName(TimeInterval interval) {
        final String functionName;
        if (interval == TimeInterval.FIVE_MINUTES) {
            functionName = TIME_SERIES_INTRADAY;
        }
        else if (interval == TimeInterval.DAILY) {
            functionName = TIME_SERIES_DAILY;
        }
        else if (interval == TimeInterval.WEEKLY) {
            functionName = TIME_SERIES_WEEKLY;
        }
        else {
            throw new IllegalArgumentException("Unsupported interval: " + interval);
        }
        return functionName;
    }

    private List<PricePoint> parseJsonToPricePoints(String jsonResponse, TimeInterval interval) {
        final List<PricePoint> pricePoints = new ArrayList<>();
        final JSONObject root = new JSONObject(jsonResponse);

        // Check for API error messages
        if (root.has(ERROR_MESSAGE_KEY) || root.has(NOTE_KEY)) {
            if (root.has(NOTE_KEY)) {
                System.out.println("API Limit Reached or Note: " + root.getString(NOTE_KEY));
            }
            else {
                throw new RuntimeException("API Error: " + root.toString());
            }
            return pricePoints;
        }

        final String timeSeriesKey = getTimeSeriesKey(interval);
        if (!root.has(timeSeriesKey)) {
            return pricePoints;
        }

        final JSONObject timeSeries = root.getJSONObject(timeSeriesKey);
        final Iterator<String> timestamps = timeSeries.keys();

        while (timestamps.hasNext()) {
            final String timestamp = timestamps.next();
            final JSONObject data = timeSeries.getJSONObject(timestamp);
            final PricePoint pricePoint = createPricePoint(timestamp, data, interval);
            pricePoints.add(pricePoint);
        }

        // Sort by time (ascending)
        pricePoints.sort((price1, price2) -> {
            return price1.getTimestamp().compareTo(price2.getTimestamp());
        });

        return pricePoints;
    }

    private PricePoint createPricePoint(String timestamp, JSONObject data, TimeInterval interval) {
        final LocalDateTime dateTime = parseTimestamp(timestamp, interval);
        final Double open = data.optDouble("1. open", 0.0);
        final Double high = data.optDouble("2. high", 0.0);
        final Double low = data.optDouble("3. low", 0.0);
        final Double close = data.optDouble("4. close", 0.0);
        final Double volume = data.optDouble("5. volume", 0.0);

        return new PricePoint(
                null, null, dateTime, interval,
                open, high, low, close, volume, "AlphaVantage"
        );
    }

    private String getTimeSeriesKey(TimeInterval interval) {
        if (interval == TimeInterval.FIVE_MINUTES) {
            return TIME_SERIES_5MIN;
        }
        else if (interval == TimeInterval.DAILY) {
            return TIME_SERIES_DAILY_KEY;
        }
        else if (interval == TimeInterval.WEEKLY) {
            return TIME_SERIES_WEEKLY_KEY;
        }
        else if (interval == TimeInterval.MONTHLY) {
            return TIME_SERIES_MONTHLY_KEY;
        }
        else {
            throw new IllegalArgumentException("Unsupported interval: " + interval);
        }
    }

    private LocalDateTime parseTimestamp(String timestamp, TimeInterval interval) {
        if (interval == TimeInterval.FIVE_MINUTES) {
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        }
        else {
            return LocalDateTime.parse(timestamp + MIDNIGHT_TIME, 
                    DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        }
    }
}
