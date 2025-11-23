package api;

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
import use_case.PriceDataAccessInterface;


public class AlphaVantagePriceGateway implements PriceDataAccessInterface {

    private final String apiKey = "3TEXXG3G3UXFI7E2";
    private static final String BASE_URL = "https://www.alphavantage.co/query?";
    private final OkHttpClient httpClient = new OkHttpClient();

    private String sendHttpRequest(String urlString) throws IOException {
        Request request = new Request.Builder()
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
        String functionName;

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

        StringBuilder urlBuilder = new StringBuilder(BASE_URL);
        urlBuilder.append("function=").append(functionName)
                .append("&symbol=").append(ticker)
                .append("&apikey=").append(apiKey);

        if (interval == TimeInterval.FIVE_MINUTES) {
            urlBuilder.append("&interval=").append("5min"); // Required parameter for intraday data
        }

        String jsonResponse = sendHttpRequest(urlBuilder.toString());

        return parseJsonToPricePoints(jsonResponse, interval);
    }


    private List<PricePoint> parseJsonToPricePoints(String jsonResponse, TimeInterval interval) {
        List<PricePoint> pricePoints = new ArrayList<>();
        
        try {
            JSONObject root = new JSONObject(jsonResponse);
            
            // Check for API error messages
            if (root.has("Error Message") || root.has("Note")) {
                throw new RuntimeException("API Error: " + root.toString());
            }
            
            // Determine the time series key based on interval
            String timeSeriesKey = getTimeSeriesKey(interval);
            
            if (!root.has(timeSeriesKey)) {
                return pricePoints; // Return empty list if no data
            }
            
            JSONObject timeSeries = root.getJSONObject(timeSeriesKey);
            Iterator<String> timestamps = timeSeries.keys();
            
            while (timestamps.hasNext()) {
                String timestamp = timestamps.next();
                JSONObject data = timeSeries.getJSONObject(timestamp);
                
                // Parse timestamp based on interval
                LocalDateTime dateTime = parseTimestamp(timestamp, interval);
                
                // Extract OHLCV data
                Double open = data.optDouble("1. open", 0.0);
                Double high = data.optDouble("2. high", 0.0);
                Double low = data.optDouble("3. low", 0.0);
                Double close = data.optDouble("4. close", 0.0);
                Double volume = data.optDouble("5. volume", 0.0);
                
                PricePoint pricePoint = new PricePoint(
                    null, null, dateTime, interval,
                    open, high, low, close, volume, "AlphaVantage"
                );
                
                pricePoints.add(pricePoint);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse price data: " + e.getMessage(), e);
        }
        
        return pricePoints;
    }
    
    private String getTimeSeriesKey(TimeInterval interval) {
        switch (interval) {
            case FIVE_MINUTES:
                return "Time Series (5min)";
            case DAILY:
                return "Time Series (Daily)";
            case WEEKLY:
                return "Weekly Time Series";
            case MONTHLY:
                return "Monthly Time Series";
            default:
                throw new IllegalArgumentException("Unsupported interval: " + interval);
        }
    }
    
    private LocalDateTime parseTimestamp(String timestamp, TimeInterval interval) {
        try {
            if (interval == TimeInterval.FIVE_MINUTES) {
                // Format: "2023-11-17 16:00:00"
                return LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } else {
                // Format: "2023-11-17" - add midnight time
                return LocalDateTime.parse(timestamp + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse timestamp: " + timestamp, e);
        }
    }
}