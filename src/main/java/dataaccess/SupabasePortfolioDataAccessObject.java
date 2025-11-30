package dataaccess;

import java.io.IOException;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SupabasePortfolioDataAccessObject {
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * Get the initial balance for a user's portfolio.
     * If no portfolio exists, returns the default initial balance.
     */
    public double getInitialBalance(UUID userId) {
        String url = EnvConfig.getSupabaseUrl() + "/rest/v1/portfolio?user_id=eq." + userId.toString();
        String serviceRoleKey = EnvConfig.getSupabaseServiceRoleKey();
        if (serviceRoleKey == null || serviceRoleKey.isEmpty()) {
            throw new RuntimeException("Supabase service role key is not set in .env");
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", serviceRoleKey)
                .addHeader("Authorization", "Bearer " + serviceRoleKey)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                ResponseBody errorBody = response.body();
                String errorResp = errorBody != null ? errorBody.string() : "No response body";
                throw new IOException("Failed to fetch portfolio: " + response.code() + " - " + errorResp);
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                return 100000.00; // Default initial balance
            }

            String resp = responseBody.string();
            if (resp.trim().equals("[]")) {
                // No portfolio found, return default
                return 100000.00;
            }

            // Parse the portfolio data
            var jsonArray = gson.fromJson(resp, com.google.gson.JsonArray.class);
            if (jsonArray.size() > 0) {
                JsonObject portfolio = jsonArray.get(0).getAsJsonObject();
                // Note: The schema shows cash_balance, but for initial balance we might want to store it separately
                // For now, we'll use cash_balance as initial balance since that's what's available
                return portfolio.get("cash_balance").getAsDouble();
            }

            return 100000.00; // Fallback

        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch portfolio via Supabase REST API", e);
        }
    }

    /**
     * Create or update a portfolio record for a user.
     * This should be called when setting up a simulation.
     */
    public void savePortfolio(UUID userId, double initialBalance) {
        // First check if portfolio exists
        String checkUrl = EnvConfig.getSupabaseUrl() + "/rest/v1/portfolio?user_id=eq." + userId.toString();
        String serviceRoleKey = EnvConfig.getSupabaseServiceRoleKey();
        if (serviceRoleKey == null || serviceRoleKey.isEmpty()) {
            throw new RuntimeException("Supabase service role key is not set in .env");
        }

        Request checkRequest = new Request.Builder()
                .url(checkUrl)
                .addHeader("apikey", serviceRoleKey)
                .addHeader("Authorization", "Bearer " + serviceRoleKey)
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        try (Response checkResponse = client.newCall(checkRequest).execute()) {
            boolean portfolioExists = false;
            if (checkResponse.isSuccessful()) {
                ResponseBody checkBody = checkResponse.body();
                if (checkBody != null) {
                    String checkResp = checkBody.string();
                    if (!checkResp.trim().equals("[]")) {
                        portfolioExists = true;
                    }
                }
            }

            JsonObject portfolioJson = new JsonObject();
            portfolioJson.addProperty("user_id", userId.toString());
            portfolioJson.addProperty("cash_balance", initialBalance);

            String url;
            RequestBody body = RequestBody.create(gson.toJson(portfolioJson), JSON);
            Request request;

            if (portfolioExists) {
                // Update existing portfolio
                url = EnvConfig.getSupabaseUrl() + "/rest/v1/portfolio?user_id=eq." + userId.toString();
                request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", serviceRoleKey)
                        .addHeader("Authorization", "Bearer " + serviceRoleKey)
                        .addHeader("Content-Type", "application/json")
                        .patch(body)
                        .build();
            } else {
                // Create new portfolio
                url = EnvConfig.getSupabaseUrl() + "/rest/v1/portfolio";
                request = new Request.Builder()
                        .url(url)
                        .addHeader("apikey", serviceRoleKey)
                        .addHeader("Authorization", "Bearer " + serviceRoleKey)
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();
            }

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    String resp = responseBody != null ? responseBody.string() : "";
                    throw new IOException("Failed to save portfolio: " + response.code() + " - " + resp);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to save portfolio via Supabase REST API", e);
        }
    }
}