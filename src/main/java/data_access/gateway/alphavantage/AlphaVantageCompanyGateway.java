package data_access.gateway.alphavantage;

import java.util.List;

import org.json.JSONObject;

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
                json.optString("Exchange", null),  // Exchange field
                (double) json.optLong("MarketCapitalization"),  // Convert to Double
                json.optDouble("EPS") != 0.0 ? json.optDouble("EPS") : null,
                json.optDouble("PERatio") != 0.0 ? json.optDouble("PERatio") : null,
                json.optDouble("DividendPerShare") != 0.0 ? json.optDouble("DividendPerShare") : null,
                json.optDouble("DividendYield") != 0.0 ? json.optDouble("DividendYield") : null,
                json.optDouble("Beta") != 0.0 ? json.optDouble("Beta") : null,
                List.of(), // Placeholder for financial statements
                List.of()  // Placeholder for news articles
        );
    }

}
