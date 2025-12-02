package dataaccess;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import entity.PricePoint;
import entity.TimeInterval;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import usecase.price_chart.PriceDataAccessInterface;

public class AlphaVantagePriceGateway implements PriceDataAccessInterface {

    private final String apiKey = EnvConfig.getAlphaVantageApiKey();
    private static final String BASE_URL = "https://www.alphavantage.co/query?";
    private static final String NOTE_KEY = "Note";
    
    private final OkHttpClient httpClient = new OkHttpClient();

    private String sendHttpRequest(String urlString) throws IOException {
        final Request request = new Request.Builder()
                .url(urlString)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API request failed with code: " + response.code());
            }
            if (response.body() == null) {
                throw new IOException("API response body is null");
            }
            return response.body().string();
        }
    }
    // -----------------------------------------------------

    @Override
    public List<PricePoint> getPriceHistory(String ticker, TimeInterval interval) throws Exception {
        final String functionName = getFunctionName(interval);
        final StringBuilder urlBuilder = new StringBuilder(BASE_URL);
        urlBuilder.append("function=").append(functionName)
                .append("&symbol=").append(ticker)
                .append("&apikey=").append(apiKey);

        if (interval == TimeInterval.FIVE_MINUTES) {
            urlBuilder.append("&interval=").append("5min");
            // Required parameter for intraday data
        }

        final String jsonResponse = sendHttpRequest(urlBuilder.toString());

        return parseJsonToPricePoints(jsonResponse, interval);
    }
    
    private String getFunctionName(TimeInterval interval) {
        final String functionName;
        switch (interval) {
            case FIVE_MINUTES:
                functionName = "TIME_SERIES_INTRADAY";
                break;
            case DAILY:
                functionName = "TIME_SERIES_DAILY";
                break;
            case WEEKLY:
                functionName = "TIME_SERIES_WEEKLY";
                break;
            default:
                throw new IllegalArgumentException("Unsupported interval: " + interval);
        }
        return functionName;
    }

    private List<PricePoint> parseJsonToPricePoints(String jsonResponse, TimeInterval interval) {
        final List<PricePoint> pricePoints = new ArrayList<>();

        final JSONObject root = new JSONObject(jsonResponse);

        // Check for API error messages
        if (!hasApiError(root)) {
            // Determine the time series key based on interval
            final String timeSeriesKey = getTimeSeriesKey(interval);

            if (root.has(timeSeriesKey)) {
                final JSONObject timeSeries = root.getJSONObject(timeSeriesKey);
                final Iterator<String> timestamps = timeSeries.keys();

                while (timestamps.hasNext()) {
                    final String timestamp = timestamps.next();
                    final JSONObject data = timeSeries.getJSONObject(timestamp);

                    // Parse timestamp based on interval
                    final LocalDateTime dateTime = parseTimestamp(timestamp, interval);

                    // Extract OHLCV data
                    final Double open = data.optDouble("1. open", 0.0);
                    final Double high = data.optDouble("2. high", 0.0);
                    final Double low = data.optDouble("3. low", 0.0);
                    final Double close = data.optDouble("4. close", 0.0);
                    final Double volume = data.optDouble("5. volume", 0.0);

                    final PricePoint pricePoint = new PricePoint(
                        null, null, dateTime, interval,
                        open, high, low, close, volume, "AlphaVantage"
                    );

                    pricePoints.add(pricePoint);
                }

                // Sort by timestamp in ascending order (oldest to newest)
                pricePoints.sort(Comparator.comparing(PricePoint::getTimestamp));
            }
        }

        return pricePoints;
    }
    
    private boolean hasApiError(JSONObject root) {
        final boolean hasError;
        if (root.has("Error Message") || root.has(NOTE_KEY)) {
            // If Note (usually API rate limit), log and return true
            if (root.has(NOTE_KEY)) {
                System.out.println("API Limit Reached or Note: " + root.getString(NOTE_KEY));
                hasError = true;
            }
            else {
                throw new RuntimeException("API Error: " + root);
            }
        }
        else {
            hasError = false;
        }
        return hasError;
    }
    
    private String getTimeSeriesKey(TimeInterval interval) {
        final String timeSeriesKey;
        switch (interval) {
            case FIVE_MINUTES:
                timeSeriesKey = "Time Series (5min)";
                break;
            case DAILY:
                timeSeriesKey = "Time Series (Daily)";
                break;
            case WEEKLY:
                timeSeriesKey = "Weekly Time Series";
                break;
            case MONTHLY:
                timeSeriesKey = "Monthly Time Series";
                break;
            default:
                throw new IllegalArgumentException("Unsupported interval: " + interval);
        }
        return timeSeriesKey;
    }
    
    private LocalDateTime parseTimestamp(String timestamp, TimeInterval interval) {
        final LocalDateTime parsedDateTime;
        try {
            if (interval == TimeInterval.FIVE_MINUTES) {
                // Format: "2023-11-17 16:00:00"
                parsedDateTime = LocalDateTime.parse(timestamp, 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            else {
                // Format: "2023-11-17" - add midnight time
                parsedDateTime = LocalDateTime.parse(timestamp + " 00:00:00", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        }
        catch (DateTimeParseException dateTimeParseException) {
            throw new RuntimeException("Failed to parse timestamp: " + timestamp, 
                dateTimeParseException);
        }
        return parsedDateTime;
    }
}
