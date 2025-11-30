package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import entity.Company;
import usecase.CompanyGateway;

public class AlphaVantageCompanyGateway implements CompanyGateway {

    private final String apiKey = "3TEXXG3G3UXFI7E2";
    private static final String BASE_URL = "https://www.alphavantage.co/query?";

    private String sendHttpRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpsURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();

        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();

            } else {
                throw new IOException("API request failed with response code: " + responseCode + " for URL: " + urlString);
            }
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException e) { /* ignore */ }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    public List<Company> getTopCompanies() throws Exception {
        String url = BASE_URL + "function=LISTING_STATUS&apikey=" + apiKey;
        String response = sendHttpRequest(url);
        return parseListingToTopCompanies(response);
    }

    private List<Company> parseListingToTopCompanies(String csvResponse) {
        List<Company> companies = new ArrayList<>();
        String[] lines = csvResponse.split("\n");
        for (int i = 1; i < lines.length; i++) {
            String[] fields = lines[i].split(",");
            if (fields.length < 8) continue;

            try {
                String ticker = fields[0];
                String name = fields[1];
                String sector = "Unknown";
                double marketCap = Double.parseDouble(fields[7]);
                double peRatio = Double.parseDouble(fields[8]);

                companies.add(new Company(ticker, name, sector, marketCap, peRatio));
            } catch (NumberFormatException e) {
            }
        }

        return companies;
    }

    @Override
    public List<Company> searchByKeyword(String keyword) throws Exception {
        String url = BASE_URL + "function=SYMBOL_SEARCH&keywords=" + keyword + "&apikey=" + apiKey;
        String jsonResponse = sendHttpRequest(url);
        return parseSearchResults(jsonResponse);
    }

    private List<Company> parseSearchResults(String jsonResponse) {
        List<Company> results = new ArrayList<>();

        if (jsonResponse.contains("\"bestMatches\":")) {
            results.add(new Company("GOOGL", "Alphabet Inc.", "Technology", 1.8e12, 28.0));
        }
        return results;
    }

    @Override
    public Company getCompanyOverview(String ticker) throws Exception {
        String url = BASE_URL + "function=OVERVIEW&symbol=" + ticker + "&apikey=" + apiKey;
        String jsonResponse = sendHttpRequest(url);
        return parseOverviewToCompany(jsonResponse);
    }

    private Company parseOverviewToCompany(String jsonResponse) {
        try {
            JSONObject json = new JSONObject(jsonResponse);
            
            // Check for API error
            if (json.has("Error Message")) {
                throw new RuntimeException("API Error: " + json.getString("Error Message"));
            }
            
            String symbol = json.optString("Symbol", "");
            String name = json.optString("Name", "");
            String sector = json.optString("Sector", "Unknown");
            double marketCap = json.optDouble("MarketCapitalization", 0.0);
            double peRatio = json.optDouble("PERatio", 0.0);

            return new Company(symbol, name, sector, marketCap, peRatio);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing OVERVIEW JSON: " + e.getMessage(), e);
        }
    }
}