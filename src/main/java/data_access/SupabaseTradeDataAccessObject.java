package data_access;


import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import entity.SimulatedTradeRecord;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import use_case.simulated_trade.SimulatedTradeDataAccessInterface;

public class SupabaseTradeDataAccessObject implements SimulatedTradeDataAccessInterface {
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Override
    public void saveTrade(SimulatedTradeRecord trade, UUID userId) {
        // Build JSON body for Supabase REST API
        JsonObject tradeJson = new JsonObject();
        tradeJson.addProperty("user_id", userId.toString());
        tradeJson.addProperty("ticker", trade.getTicker());
        tradeJson.addProperty("is_long", trade.isLong());
        tradeJson.addProperty("quantity", trade.getQuantity());
        tradeJson.addProperty("entry_price", trade.getEntryPrice());
        tradeJson.addProperty("exit_price", trade.getExitPrice());
        tradeJson.addProperty("realized_pnl", trade.getRealizedPnL());
        // Format timestamps as ISO 8601 for Supabase
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        tradeJson.addProperty("entry_time", trade.getEntryTime().format(formatter));
        tradeJson.addProperty("exit_time", trade.getExitTime().format(formatter));

        String url = EnvConfig.getSupabaseUrl() + "/rest/v1/trades";
        String serviceRoleKey = EnvConfig.getSupabaseServiceRoleKey();
        if (serviceRoleKey == null || serviceRoleKey.isEmpty()) {
            throw new RuntimeException("Supabase service role key is not set in .env");
        }

        RequestBody body = RequestBody.create(gson.toJson(tradeJson), JSON);
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", serviceRoleKey)
                .addHeader("Authorization", "Bearer " + serviceRoleKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to store trade: " + response.code() + " - " + response.body().string());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to store trade via Supabase REST API", e);
        }
    }
}