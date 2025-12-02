package dataaccess;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import api.Api;
import entity.EconomicIndicator;
import usecase.EconomicIndicatorGateway;

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

        }
        catch (Exception ex) {
            System.err.println("⚠️ Error fetching economic indicators: " + ex.getMessage());
            // Return partial data if some indicators failed
            if (indicators.isEmpty()) {
                throw ex;
            }
        }

        return indicators;
    }

    /**
     * Fetches the current Federal Funds Rate.
     *
     * <p>
     * This method requests the "FEDERAL_FUNDS_RATE" indicator from the API using a
     * monthly interval. If the API returns valid data, the most recent entry is parsed
     * and returned as an {@link EconomicIndicator}. If the API request fails or the
     * expected data is missing, a predefined fallback value is returned instead.
     *
     * @return an {@link EconomicIndicator} containing the latest Federal Funds Rate,
     *         or a fallback indicator if the API request fails
     */
    private EconomicIndicator fetchFederalFundsRate() {
        try {
            System.out.println("    → Fetching Federal Funds Rate...");
            String jsonString = api.getEconomicIndicator("FEDERAL_FUNDS_RATE", "monthly");
            JSONObject json = new JSONObject(jsonString);

            if (json.has("data")) {
                JSONArray data = json.getJSONArray("data");
                if (!data.isEmpty()) {
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

        }
        catch (Exception ex) {
            System.out.println("    ⚠️ Federal Funds Rate fetch failed: " + ex.getMessage());
            System.out.println("    → Using fallback data");
            return new EconomicIndicator("Federal Funds Rate", "4.33 %", "2025-11-01", "FEDERAL_FUNDS_RATE");
        }
    }

    /**
     * Fetches the Real Gross Domestic Product (Real GDP) for the United States.
     *
     * <p>
     * This method retrieves the "REAL_GDP" indicator from the API using quarterly data.
     * If valid data is returned, the most recent GDP value (reported in billions of USD)
     * is converted into trillions of USD and formatted. The result is returned as an
     * {@link EconomicIndicator}. If the API request fails or contains no usable data,
     * a predefined fallback GDP value is returned instead.
     *
     * @return an {@link EconomicIndicator} representing the latest Real GDP value,
     *         or a fallback indicator if the API request fails or returns no data
     */
    private EconomicIndicator fetchRealGDP() {
        try {
            System.out.println("    → Fetching Real GDP...");
            String jsonString = api.getEconomicIndicator("REAL_GDP", "quarterly");
            JSONObject json = new JSONObject(jsonString);

            if (json.has("data")) {
                JSONArray data = json.getJSONArray("data");
                if (!data.isEmpty()) {
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

        }
        catch (Exception ex) {
            System.out.println("    ⚠️ Real GDP fetch failed: " + ex.getMessage());
            System.out.println("    → Using fallback data");
            return new EconomicIndicator("U.S. Real GDP", "27.36 Trillion USD", "2025-04-01", "REAL_GDP");
        }
    }

    /**
     * Fetches the U.S. unemployment rate.
     *
     * <p>
     * This method requests the "UNEMPLOYMENT" economic indicator from the API
     * using monthly data. If valid results are returned, the most recent unemployment
     * rate is extracted and returned as an {@link EconomicIndicator}. If the API call
     * fails or the data is missing or empty, a predefined fallback unemployment rate
     * is used instead.
     *
     * @return an {@link EconomicIndicator} representing the latest unemployment rate,
     *         or a fallback indicator if the API request fails or provides no data
     */
    private EconomicIndicator fetchUnemploymentRate() {
        try {
            System.out.println("    → Fetching Unemployment Rate...");
            String jsonString = api.getEconomicIndicator("UNEMPLOYMENT", "monthly");
            JSONObject json = new JSONObject(jsonString);

            if (json.has("data")) {
                JSONArray data = json.getJSONArray("data");
                if (!data.isEmpty()) {
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

        }
        catch (Exception ex) {
            System.out.println("    ⚠️ Unemployment Rate fetch failed: " + ex.getMessage());
            System.out.println("    → Using fallback data");
            return new EconomicIndicator("Unemployment Rate", "3.7 %", "2025-10-01", "UNEMPLOYMENT");
        }
    }

    /**
     * Fetches the 10-year U.S. Treasury yield.
     *
     * <p>
     * This method requests the "TREASURY_YIELD" economic indicator from the API
     * using monthly data. If the API returns valid results, the most recent yield
     * value is extracted and returned as an {@link EconomicIndicator}. If the API
     * call fails or provides no usable data, a predefined fallback Treasury yield
     * is returned instead.
     *
     * @return an {@link EconomicIndicator} representing the latest 10-year Treasury yield,
     *         or a fallback indicator if the API request fails or provides no data
     */
    private EconomicIndicator fetchTreasuryYield() {
        try {
            System.out.println("    → Fetching Treasury Yield...");
            String jsonString = api.getEconomicIndicator("TREASURY_YIELD", "monthly");
            JSONObject json = new JSONObject(jsonString);

            if (json.has("data")) {
                JSONArray data = json.getJSONArray("data");
                if (!data.isEmpty()) {
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

        }
        catch (Exception ex) {
            System.out.println("    ⚠️ Treasury Yield fetch failed: " + ex.getMessage());
            System.out.println("    → Using fallback data");
            return new EconomicIndicator("Treasury Yield (10Y)", "4.5 %", "2025-11-20", "TREASURY_YIELD");
        }
    }

    /**
     * Fetch Consumer Price Index (CPI).
     *
     * @return the CPI, or a fallback indicator if the API request fails or provides no data
     */
    private EconomicIndicator fetchCPI() {
        try {
            System.out.println("    → Fetching CPI...");
            String jsonString = api.getEconomicIndicator("CPI", "monthly");
            JSONObject json = new JSONObject(jsonString);

            if (json.has("data")) {
                JSONArray data = json.getJSONArray("data");
                if (!data.isEmpty()) {
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

        }
        catch (Exception ex) {
            System.out.println("    ⚠️ CPI fetch failed: " + ex.getMessage());
            System.out.println("    → Using fallback data");
            return new EconomicIndicator("Consumer Price Index", "3.2 %", "2025-10-01", "CPI");
        }
    }

    /**
     * Fetch Inflation Rate.
     * @return Economic indicator inflation rate, or a fallback indicator if the API request fails or provides no data
     */
    private EconomicIndicator fetchInflationRate() {
        try {
            System.out.println("    → Fetching Inflation Rate...");
            String jsonString = api.getEconomicIndicator("INFLATION", "annual");
            JSONObject json = new JSONObject(jsonString);

            if (json.has("data")) {
                JSONArray data = json.getJSONArray("data");
                if (!data.isEmpty()) {
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

        }
        catch (Exception ex) {
            System.out.println("    ⚠️ Inflation Rate fetch failed: " + ex.getMessage());
            System.out.println("    → Using fallback data");
            return new EconomicIndicator("Inflation Rate", "2.4 %", "2025-11-20", "INFLATION");
        }
    }
}
