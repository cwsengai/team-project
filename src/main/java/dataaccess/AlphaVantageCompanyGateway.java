package dataaccess;

import java.util.List;

import org.json.JSONObject;

import api.Api;
import entity.Company;
import usecase.company.CompanyGateway;

public class AlphaVantageCompanyGateway implements CompanyGateway {
    private final Api api;

    public AlphaVantageCompanyGateway(Api api) {
        this.api = api;
    }

    @Override
    public Company fetchOverview(String symbol) {
        final String jsonString;
        try {
            jsonString = api.getOverview(symbol);
        }
        catch (Exception ex) {
            // handle or log as appropriate; returning null for now
            System.err.println("AlphaVantageCompanyGateway.fetchOverview error: " + ex.getMessage());
            for (StackTraceElement ste : ex.getStackTrace()) {
                System.err.println("    at " + ste.toString());
            }
            return null;
        }

        if (jsonString == null) {
            return null;
        }

        final JSONObject json = new JSONObject(jsonString);

        return new Company(
                json.optString("Symbol"),
                json.optString("Name"),
                json.optString("Description"),
                json.optString("Sector"),
                json.optString("Industry"),
                json.optString("Country"),
                json.optLong("MarketCapitalization"),
                (float) json.optDouble("EPS"),
                (float) json.optDouble("PERatio"),
                (float) json.optDouble("DividendPerShare"),
                (float) json.optDouble("DividendYield"),
                (float) json.optDouble("Beta"),
                // Placeholder for financial statements
                List.of(),
                // Placeholder for news articles
                List.of()
        );
    }

}
