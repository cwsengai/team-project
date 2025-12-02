package dataaccess;

import entity.EconomicIndicator;
import org.json.JSONArray;
import org.json.JSONObject;
import usecase.EconomicIndicatorGateway;
import api.Api;

import java.util.ArrayList;
import java.util.List;

/**
 * Gateway for fetching economic indicators from Alpha Vantage API.
 * Fetches real-time economic data including Fed rates, GDP, unemployment, etc.
 */
public class AlphaVantageEconomicIndicatorGateway implements EconomicIndicatorGateway {

    private final Api api;

    public AlphaVantageEconomicIndicatorGateway(Api api) {
        this.api = api;
    }

    @Override
    public List<EconomicIndicator> getEconomicIndicators() throws Exception {
        List<EconomicIndicator> indicators = new ArrayList<>();

        try {
            // Fetch each economic indicator from API
            System.out.println("  Fetching economic indicators from API...");
            indicators.add(fetchFederalFundsRate());
            indicators.add(fetchRealGDP());
            indicators.add(fetchUnemploymentRate());
            indicators.add(fetchTreasuryYield());
            indicators.add(fetchCPI());
            indicators.add(fetchInflationRate());

        } catch (Exception e) {
            System.err.println("⚠️ Error fetching economic indicators: " + e.getMessage());
            // Return partial data if some indicators failed
            if (indicators.isEmpty()) {
                throw e; // Re-throw if no data at all
            }
        }

        return indicators;
    }

    /**
     * Fetch Federal Funds Rate.
     */
    private EconomicIndicator fetchFederalFundsRate() throws Exception {
        try {
            System.out.println("    → Fetching Federal Funds Rate...");
            String jsonString = api.getEconomicIndicator("FEDERAL_FUNDS_RATE", "monthly");
            JSONObject json = new JSONObject(jsonString);

            if (json.has("data")) {
                JSONArray data = json.getJSONArray("data");
                if (data.length() > 0) {
                    JSONObject latest = data.getJSONObject(0);
                    String value = latest.getString("value");
                    String date = latest.getString("date");

                    System.out.println("    ✅ Federal Funds Rate: " + value + "% (from API)");
                    return new EconomicIndicator(
                            "Federal Funds Rate",
                            value + " %",
                            date,
                            "FEDERAL_FUNDS_RATE"
                    );
                }
            }

            // Fallback if API fails
            System.out.println("    ⚠️ Federal Funds Rate: Using fallback data");
            return new EconomicIndicator("Federal Funds Rate", "4.33 %", "2025-11-01", "FEDERAL_FUNDS_RATE");

        } catch (Exception e) {
            System.out.println("    ⚠️ Federal Funds Rate fetch failed: " + e.getMessage());
            System.out.println("    → Using fallback data");
            return new EconomicIndicator("Federal Funds Rate", "4.33 %", "2025-11-01", "FEDERAL_FUNDS_RATE");
        }
    }

    /**
     * Fetch Real GDP.
     */
    private EconomicIndicator fetchRealGDP() throws Exception {
        try {
            System.out.println("    → Fetching Real GDP...");
            String jsonString = api.getEconomicIndicator("REAL_GDP", "quarterly");
            JSONObject json = new JSONObject(jsonString);

            if (json.has("data")) {
                JSONArray data = json.getJSONArray("data");
                if (data.length() > 0) {
                    JSONObject latest = data.getJSONObject(0);
                    String value = latest.getString("value");
                    String date = latest.getString("date");

                    // Convert to trillions
                    double gdpBillions = Double.parseDouble(value);
                    double gdpTrillions = gdpBillions / 1000.0;
                    String formatted = String.format("%.2f Trillion USD", gdpTrillions);

                    System.out.println("    ✅ Real GDP: " + formatted + " (from API)");
                    return new EconomicIndicator(
                            "U.S. Real GDP",
                            formatted,
                            date,
                            "REAL_GDP"
                    );
                }
            }

            System.out.println("    ⚠️ Real GDP: Using fallback data");
            return new EconomicIndicator("U.S. Real GDP", "27.36 Trillion USD", "2025-04-01", "REAL_GDP");

        } catch (Exception e) {
            System.out.println("    ⚠️ Real GDP fetch failed: " + e.getMessage());
            System.out.println("    → Using fallback data");
            return new EconomicIndicator("U.S. Real GDP", "27.36 Trillion USD", "2025-04-01", "REAL_GDP");
        }
    }

    /**
     * Fetch Unemployment Rate.
     */
    private EconomicIndicator fetchUnemploymentRate() throws Exception {
        try {
            System.out.println("    → Fetching Unemployment Rate...");
            String jsonString = api.getEconomicIndicator("UNEMPLOYMENT", "monthly");
            JSONObject json = new JSONObject(jsonString);

            if (json.has("data")) {
                JSONArray data = json.getJSONArray("data");
                if (data.length() > 0) {
                    JSONObject latest = data.getJSONObject(0);
                    String value = latest.getString("value");
                    String date = latest.getString("date");

                    System.out.println("    ✅ Unemployment Rate: " + value + "% (from API)");
                    return new EconomicIndicator(
                            "Unemployment Rate",
                            value + " %",
                            date,
                            "UNEMPLOYMENT"
                    );
                }
            }

            System.out.println("    ⚠️ Unemployment Rate: Using fallback data");
            return new EconomicIndicator("Unemployment Rate", "3.7 %", "2025-10-01", "UNEMPLOYMENT");

        } catch (Exception e) {
            System.out.println("    ⚠️ Unemployment Rate fetch failed: " + e.getMessage());
            System.out.println("    → Using fallback data");
            return new EconomicIndicator("Unemployment Rate", "3.7 %", "2025-10-01", "UNEMPLOYMENT");
        }
    }

    /**
     * Fetch Treasury Yield (10-Year).
     */
    private EconomicIndicator fetchTreasuryYield() throws Exception {
        try {
            System.out.println("    → Fetching Treasury Yield...");
            String jsonString = api.getEconomicIndicator("TREASURY_YIELD", "monthly");
            JSONObject json = new JSONObject(jsonString);

            if (json.has("data")) {
                JSONArray data = json.getJSONArray("data");
                if (data.length() > 0) {
                    JSONObject latest = data.getJSONObject(0);
                    String value = latest.getString("value");
                    String date = latest.getString("date");

                    System.out.println("    ✅ Treasury Yield: " + value + "% (from API)");
                    return new EconomicIndicator(
                            "Treasury Yield (10Y)",
                            value + " %",
                            date,
                            "TREASURY_YIELD"
                    );
                }
            }

            System.out.println("    ⚠️ Treasury Yield: Using fallback data");
            return new EconomicIndicator("Treasury Yield (10Y)", "4.5 %", "2025-11-20", "TREASURY_YIELD");

        } catch (Exception e) {
            System.out.println("    ⚠️ Treasury Yield fetch failed: " + e.getMessage());
            System.out.println("    → Using fallback data");
            return new EconomicIndicator("Treasury Yield (10Y)", "4.5 %", "2025-11-20", "TREASURY_YIELD");
        }
    }

    /**
     * Fetch Consumer Price Index (CPI).
     */
    private EconomicIndicator fetchCPI() throws Exception {
        try {
            System.out.println("    → Fetching CPI...");
            String jsonString = api.getEconomicIndicator("CPI", "monthly");
            JSONObject json = new JSONObject(jsonString);

            if (json.has("data")) {
                JSONArray data = json.getJSONArray("data");
                if (data.length() > 0) {
                    JSONObject latest = data.getJSONObject(0);
                    String value = latest.getString("value");
                    String date = latest.getString("date");

                    System.out.println("    ✅ CPI: " + value + " (from API)");
                    return new EconomicIndicator(
                            "Consumer Price Index",
                            value,
                            date,
                            "CPI"
                    );
                }
            }

            System.out.println("    ⚠️ CPI: Using fallback data");
            return new EconomicIndicator("Consumer Price Index", "3.2 %", "2025-10-01", "CPI");

        } catch (Exception e) {
            System.out.println("    ⚠️ CPI fetch failed: " + e.getMessage());
            System.out.println("    → Using fallback data");
            return new EconomicIndicator("Consumer Price Index", "3.2 %", "2025-10-01", "CPI");
        }
    }

    /**
     * Fetch Inflation Rate.
     */
    private EconomicIndicator fetchInflationRate() throws Exception {
        try {
            System.out.println("    → Fetching Inflation Rate...");
            String jsonString = api.getEconomicIndicator("INFLATION", "annual");
            JSONObject json = new JSONObject(jsonString);

            if (json.has("data")) {
                JSONArray data = json.getJSONArray("data");
                if (data.length() > 0) {
                    JSONObject latest = data.getJSONObject(0);
                    String value = latest.getString("value");
                    String date = latest.getString("date");

                    System.out.println("    ✅ Inflation Rate: " + value + "% (from API)");
                    return new EconomicIndicator(
                            "Inflation Rate",
                            value + " %",
                            date,
                            "INFLATION"
                    );
                }
            }

            System.out.println("    ⚠️ Inflation Rate: Using fallback data");
            return new EconomicIndicator("Inflation Rate", "2.4 %", "2025-11-20", "INFLATION");

        } catch (Exception e) {
            System.out.println("    ⚠️ Inflation Rate fetch failed: " + e.getMessage());
            System.out.println("    → Using fallback data");
            return new EconomicIndicator("Inflation Rate", "2.4 %", "2025-11-20", "INFLATION");
        }
    }
}
