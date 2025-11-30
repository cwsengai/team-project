package usecase.simulated_trade;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import data_access.EnvConfig;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SupabaseTestUtils {
    private static final String SUPABASE_URL = EnvConfig.getSupabaseUrl();
    private static final String SUPABASE_ANON_KEY = EnvConfig.getSupabaseAnonKey();
    private static final OkHttpClient client = new OkHttpClient();

    // Set to false to skip user cleanup after test
    public static final boolean CLEANUP_USER_AFTER = false;

    public static String createUserAndGetJwt(String email, String password) throws IOException {
        // 1. Sign up user
        String signupUrl = SUPABASE_URL + "/auth/v1/signup";
        JsonObject signupBody = new JsonObject();
        signupBody.addProperty("email", email);
        signupBody.addProperty("password", password);
        Request signupRequest = new Request.Builder()
                .url(signupUrl)
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(signupBody.toString(), MediaType.get("application/json")))
                .build();
        try (Response response = client.newCall(signupRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to sign up user: " + response.code() + " - " + response.body().string());
            }
        }
        // 2. Log in user
        String loginUrl = SUPABASE_URL + "/auth/v1/token?grant_type=password";
        JsonObject loginBody = new JsonObject();
        loginBody.addProperty("email", email);
        loginBody.addProperty("password", password);
        Request loginRequest = new Request.Builder()
                .url(loginUrl)
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(loginBody.toString(), MediaType.get("application/json")))
                .build();
        try (Response response = client.newCall(loginRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to log in user: " + response.code() + " - " + response.body().string());
            }
            ResponseBody responseBodyObj = response.body();
            if (responseBodyObj == null) {
                throw new IOException("Login response body is null");
            }
            String responseBody = responseBodyObj.string();
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            return json.get("access_token").getAsString();
        }
    }
}
