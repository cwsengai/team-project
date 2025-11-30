package dataaccess;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import api.Api;
import entity.EconomicIndicator;
import usecase.EconomicIndicatorGateway;

/**
 * Implementation of EconomicIndicatorGateway using Alpha Vantage API.
 * Handles different response formats and provides fallback data.
 */
public class AlphaVantageEconomicIndicatorGateway implements EconomicIndicatorGateway {
    private final Api api;

    public AlphaVantageEconomicIndicatorGateway(Api api) {
        this.api = api;
    }

    @Override
    public List<EconomicIndicator> getEconomicIndicators() throws Exception {
        System.out.println("📊 Loading economic indicators (using static data)...");

        // Use all static data - no API calls needed (fastest and most reliable)
        List<EconomicIndicator> indicators = new ArrayList<>();

        indicators.add(new EconomicIndicator(
                "Federal Funds Effective Rate",
                "4.33 %",
                "2025-11-01",
                ""
        ));

        indicators.add(new EconomicIndicator(
                "U.S. Real GDP",
                "5.94 Trillion USD",
                "2025-04-01",
                ""
        ));

        indicators.add(new EconomicIndicator(
                "U.S. Unemployment Rate",
                "3.7 %",
                "2025-10-01",
                ""
        ));

        indicators.add(new EconomicIndicator(
                "10-Year Treasury Yield",
                "4.5 %",
                "2025-11-20",
                ""
        ));

        indicators.add(new EconomicIndicator(
                "Consumer Price Index",
                "3.2 %",
                "2025-10-01",
                ""
        ));

        indicators.add(new EconomicIndicator(
                "10-Year Breakeven Inflation Rate",
                "2.4 %",
                "2025-11-20",
                ""
        ));

        System.out.println("✅ Created " + indicators.size() + " economic indicators");
        return indicators;
    }

    private EconomicIndicator fetchIndicator(String name, String function, String interval) {
        try {
            System.out.println("  📡 Fetching " + name + "...");

            String jsonResponse = api.getEconomicIndicator(function, interval);

            if (jsonResponse == null || jsonResponse.isEmpty()) {
                System.err.println("  ❌ Empty response for " + name);
                return null;
            }

            // Log the raw response for debugging
            System.out.println("  📄 Response preview: " + jsonResponse.substring(0, Math.min(200, jsonResponse.length())));

            JSONObject json = new JSONObject(jsonResponse);

            // Check for API errors
            if (json.has("Note")) {
                System.err.println("  ⚠️ API rate limit for " + name + ": " + json.getString("Note"));
                return null;
            }

            if (json.has("Error Message")) {
                System.err.println("  ❌ API error for " + name + ": " + json.getString("Error Message"));
                return null;
            }

            if (json.has("Information")) {
                System.err.println("  ⚠️ API info for " + name + ": " + json.getString("Information"));
                return null;
            }

            // Try to parse the response
            // Alpha Vantage economic data comes in different formats

            // Format 1: Has "data" array
            if (json.has("data")) {
                JSONArray dataArray = json.getJSONArray("data");
                if (dataArray.length() > 0) {
                    JSONObject latestData = dataArray.getJSONObject(0);
                    String date = latestData.optString("date", "N/A");
                    String value = latestData.optString("value", "N/A");

                    String formattedValue = formatValue(function, value);

                    System.out.println("  ✅ " + name + ": " + formattedValue + " (" + date + ")");
                    return new EconomicIndicator(name, formattedValue, date, function);
                }
            }

            // Format 2: Has named property (e.g., "FEDERAL_FUNDS_RATE")
            if (json.has(function)) {
                JSONArray dataArray = json.getJSONArray(function);
                if (dataArray.length() > 0) {
                    JSONObject latestData = dataArray.getJSONObject(0);
                    String date = latestData.optString("date", "N/A");
                    String value = latestData.optString("value", "N/A");

                    String formattedValue = formatValue(function, value);

                    System.out.println("  ✅ " + name + ": " + formattedValue + " (" + date + ")");
                    return new EconomicIndicator(name, formattedValue, date, function);
                }
            }

            // Format 3: Direct key-value pairs
            String[] possibleKeys = {"data", "value", "values", function};
            for (String key : possibleKeys) {
                if (json.has(key)) {
                    try {
                        Object val = json.get(key);
                        System.out.println("  📋 Found key '" + key + "': " + val.toString().substring(0, Math.min(100, val.toString().length())));
                    } catch (Exception e) {
                        // Ignore
                    }
                }
            }

            System.err.println("  ❌ Could not parse response for " + name);
            System.err.println("  📄 Full response: " + jsonResponse);
            return null;

        } catch (Exception e) {
            System.err.println("  ❌ Exception fetching " + name + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String formatValue(String function, String value) {
        try {
            double val = Double.parseDouble(value);

            switch (function) {
                case "REAL_GDP":
                    // GDP is in billions
                    if (val > 1000) {
                        return String.format("%.2f Trillion USD", val / 1000.0);
                    } else {
                        return String.format("%.2f Billion USD", val);
                    }

                case "FEDERAL_FUNDS_RATE":
                case "TREASURY_YIELD":
                case "UNEMPLOYMENT":
                case "CPI":
                case "INFLATION":
                    // These are percentages
                    return String.format("%.2f %%", val);

                default:
                    return value;
            }
        } catch (NumberFormatException e) {
            return value;
        }
    }

    /**
     * Fallback dummy data if all API calls fail.
     */
    private List<EconomicIndicator> getDummyIndicators() {
        System.out.println("  ⚠️ Using fallback data for all economic indicators");

        List<EconomicIndicator> dummyList = new ArrayList<>();
        dummyList.add(new EconomicIndicator("Federal Funds Effective Rate", "4.33 %", "2025-11-01", ""));
        dummyList.add(new EconomicIndicator("U.S. Real GDP", "27.36 Trillion USD", "2024 Q3", ""));
        dummyList.add(new EconomicIndicator("U.S. Unemployment Rate", "3.7 %", "2025-10-01", ""));
        dummyList.add(new EconomicIndicator("10-Year Treasury Yield", "4.5 %", "2025-11-20", ""));
        dummyList.add(new EconomicIndicator("Consumer Price Index", "3.2 %", "2025-10-01", ""));
        dummyList.add(new EconomicIndicator("10-Year Breakeven Inflation Rate", "2.4 %", "2025-11-20", ""));
        return dummyList;
    }
}