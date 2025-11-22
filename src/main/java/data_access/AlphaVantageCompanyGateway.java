package data_access;

import org.json.JSONObject;
import java.util.List;

import api.Api;

import entity.Company;
import use_case.company.CompanyGateway;


public class AlphaVantageCompanyGateway implements CompanyGateway {
    private final Api api;

    public AlphaVantageCompanyGateway(Api api) {
        this.api = api;
    }

    @Override
    public Company fetchOverview(String symbol) {
        String jsonString;
        try {
            jsonString = api.getOverview(symbol);
        } catch (Exception e) {
            // handle or log as appropriate; returning null for now
            e.printStackTrace();
            return null;
        }

        if (jsonString == null) {
            return null;
        }

        JSONObject json = new JSONObject(jsonString);

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
                List.of(), // Placeholder for financial statements
                List.of()  // Placeholder for news articles
        );
    }

}
