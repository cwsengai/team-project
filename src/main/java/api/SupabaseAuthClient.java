package api;

import java.io.IOException;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SupabaseAuthClient {

    private final String supabaseApiKey;
    private final String supabaseUrl;
    private final OkHttpClient client = new OkHttpClient();

    public SupabaseAuthClient(String supabaseUrl, String supabaseApiKey) {
        this.supabaseUrl = supabaseUrl;
        this.supabaseApiKey = supabaseApiKey;
    }

    /**
     * Registers a new user using the provided email and password.
     *
     * @param email the user's email address
     * @param password the user's chosen password
     * @return a JSON object containing the authentication response
     * @throws IOException if an error occurs while communicating with the authentication service
     */
    public JSONObject signUp(String email, String password) throws IOException {
        final String url = supabaseUrl + "/auth/v1/signup";
        return sendAuthRequest(url, email, password);
    }

    /**
     * Authenticates an existing user with the given email and password.
     *
     * @param email the user's email address
     * @param password the user's password
     * @return a JSON object containing the authentication response
     * @throws IOException if an error occurs while communicating with the authentication service
     */
    public JSONObject signIn(String email, String password) throws IOException {
        final String url = supabaseUrl + "/auth/v1/token?grant_type=password";
        return sendAuthRequest(url, email, password);
    }

    private JSONObject sendAuthRequest(String url, String email, String password) throws IOException {
        final JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);
        final RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));
        final Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseApiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            final ResponseBody responseBody = response.body();
            final String resp = (responseBody != null) ? responseBody.string() : "";
            return new JSONObject(resp);
        }
    }

}
