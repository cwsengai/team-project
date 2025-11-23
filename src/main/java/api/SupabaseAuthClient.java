package api;

import java.io.IOException;
import java.util.UUID;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SupabaseAuthClient {
        /**
         * Signs in with the given email and password and returns the JWT (access_token), or null on failure.
         */
        public String loginAndGetJwt(String email, String password) throws IOException {
            JSONObject result = signIn(email, password);
            return result.optString("access_token", null);
        }
    private final String supabaseUrl;
    private final String supabaseApiKey;
    private final OkHttpClient client = new OkHttpClient();

    public SupabaseAuthClient(String supabaseUrl, String supabaseApiKey) {
        this.supabaseUrl = supabaseUrl;
        this.supabaseApiKey = supabaseApiKey;
    }

    public JSONObject signUp(String email, String password) throws IOException {
        String url = supabaseUrl + "/auth/v1/signup";
        return sendAuthRequest(url, email, password);
    }

    public JSONObject signIn(String email, String password) throws IOException {
        String url = supabaseUrl + "/auth/v1/token?grant_type=password";
        return sendAuthRequest(url, email, password);
    }

    private JSONObject sendAuthRequest(String url, String email, String password) throws IOException {
        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);
        RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseApiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            String resp = (responseBody != null) ? responseBody.string() : "";
            return new JSONObject(resp);
        }
    }

    /**
     * Utility to create a random user (email, password) and sign up or sign in, returning the JWT.
     */
    public String createRandomUserAndGetJwt() throws IOException {
        String email = "user_" + UUID.randomUUID().toString().replace("-", "") + "@example.com";
        String password = UUID.randomUUID().toString().replace("-", "");
        JSONObject result;
        try {
            result = signUp(email, password);
        } catch (IOException | org.json.JSONException e) {
            // If user exists, try sign in
            result = signIn(email, password);
        }
        // JWT is in result["access_token"]
        return result.optString("access_token", null);
    }
}
