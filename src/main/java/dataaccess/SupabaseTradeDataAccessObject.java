
package dataaccess;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import entity.SimulatedTradeRecord;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import usecase.simulated_trade.SimulatedTradeDataAccessInterface;

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
                ResponseBody responseBody = response.body();
                String resp;
                if (responseBody != null) {
                    resp = responseBody.string();
                } else {
                    resp = "";
                }
                throw new IOException("Failed to store trade: " + response.code() + " - " + resp);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to store trade via Supabase REST API", e);
        }
    }

    /**
     * Fetches all trades for a given user from Supabase.
     */
    public List<SimulatedTradeRecord> fetchTradesForUser(UUID userId) {
        List<SimulatedTradeRecord> trades = new ArrayList<>();
        String url = EnvConfig.getSupabaseUrl() + "/rest/v1/trades?user_id=eq." + userId.toString();
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
                throw new IOException("Failed to fetch trades: " + response.code());
            }
            ResponseBody responseBody = response.body();
            if (responseBody == null) return trades;
            String resp = responseBody.string();
            JsonArray arr = gson.fromJson(resp, JsonArray.class);
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                try {
                    SimulatedTradeRecord record = new SimulatedTradeRecord(
                        obj.get("ticker").getAsString(),
                        obj.get("is_long").getAsBoolean(),
                        obj.get("quantity").getAsInt(),
                        obj.get("entry_price").getAsDouble(),
                        obj.get("exit_price").getAsDouble(),
                        obj.get("realized_pnl").getAsDouble(),
                        LocalDateTime.parse(obj.get("entry_time").getAsString(), formatter),
                        LocalDateTime.parse(obj.get("exit_time").getAsString(), formatter),
                        obj.get("user_id").getAsString()
                    );
                    trades.add(record);
                } catch (Exception parseEx) {
                    // skip malformed record
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch trades via Supabase REST API", e);
        }
        return trades;
    }
}
