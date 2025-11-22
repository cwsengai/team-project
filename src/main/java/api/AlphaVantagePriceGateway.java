package api;

import use_case.PriceDataAccessInterface;
import entity.PricePoint;
import entity.TimeInterval;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;


public class AlphaVantagePriceGateway implements PriceDataAccessInterface {

    private final String apiKey = "3TEXXG3G3UXFI7E2";
    private static final String BASE_URL = "https://www.alphavantage.co/query?";

    private String sendHttpRequest(String urlString) throws IOException {
        System.out.println("DEBUG: Sending API request for price history: " + urlString);
        return "{}";
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
            urlBuilder.append("&interval=").append("5min"); // 盘中数据所需参数
        }

        String jsonResponse = sendHttpRequest(urlBuilder.toString());

        return parseJsonToPricePoints(jsonResponse, interval);
    }


    private List<PricePoint> parseJsonToPricePoints(String jsonResponse, TimeInterval interval) {

        /*
         * Actual Implementation Steps:
         * 1. Introduce Jackson/Gson libraries.
         * 2. Parse the jsonResponse string.
         * 3. Iterate through timestamps under the "Time Series (X)" field.
         * 4. Extract fields such as "1. open", "4. close", and "5. volume".
         * 5. Create new PricePoint entities and add them to the list.
         */

        // We return an empty list to ensure compilation, due to the lack of external libraries.
        return new ArrayList<>();
    }
}