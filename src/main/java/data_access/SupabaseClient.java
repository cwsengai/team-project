package data_access;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Client for interacting with Supabase REST API and authentication.
 * Handles JWT-based authentication and database operations via PostgREST.
 */
public class SupabaseClient {
    private static final String DEFAULT_SUPABASE_URL = EnvConfig.getSupabaseUrl();
    private static final String DEFAULT_SUPABASE_ANON_KEY = EnvConfig.getSupabaseAnonKey();
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");

    private final String supabaseUrl;
    private final String anonKey;
    private final OkHttpClient httpClient;
    private final Gson gson;
    private String accessToken; // JWT from auth

    /**
     * Creates a new Supabase client with default settings.
     */
    public SupabaseClient() {
        this(DEFAULT_SUPABASE_URL, DEFAULT_SUPABASE_ANON_KEY);
    }

    /**
     * Creates a new Supabase client with custom URL and API key.
     * Use this constructor to override the default Supabase project settings.
     *
     * @param supabaseUrl the Supabase project URL
     * @param anonKey the anonymous API key
     */
    public SupabaseClient(String supabaseUrl, String anonKey) {
        this.supabaseUrl = supabaseUrl;
        this.anonKey = anonKey;
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        this.gson = new Gson();
    }

    // ============================================================================
    // AUTHENTICATION
    // ============================================================================

    /**
     * Sign up a new user with email and password.
     *
     * @param email the user's email
     * @param password the user's password
     * @return authentication response with access token and user info
     * @throws IOException if the request fails
     */
    public AuthResponse signUp(String email, String password) throws IOException {
        String json = String.format(
            "{\"email\":\"%s\",\"password\":\"%s\"}",
            email, password
        );

        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
            .url(supabaseUrl + "/auth/v1/signup")
            .header("apikey", anonKey)
            .post(body)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new IOException("Sign up failed: " + responseBody);
            }
            AuthResponse authResponse = gson.fromJson(responseBody, AuthResponse.class);
            this.accessToken = authResponse.getAccessToken();
            return authResponse;
        }
    }

    /**
     * Sign in an existing user with email and password.
     *
     * @param email the user's email
     * @param password the user's password
     * @return authentication response with access token and user info
     * @throws IOException if the request fails
     */
    public AuthResponse signIn(String email, String password) throws IOException {
        String json = String.format(
            "{\"email\":\"%s\",\"password\":\"%s\"}",
            email, password
        );

        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
            .url(supabaseUrl + "/auth/v1/token?grant_type=password")
            .header("apikey", anonKey)
            .post(body)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new IOException("Sign in failed: " + responseBody);
            }
            AuthResponse authResponse = gson.fromJson(responseBody, AuthResponse.class);
            this.accessToken = authResponse.getAccessToken();
            return authResponse;
        }
    }

    /**
     * Sign out the current user.
     * Clears the access token from the client.
     */
    public void signOut() {
        this.accessToken = null;
    }

    /**
     * Check if a user is currently authenticated.
     *
     * @return true if access token exists, false otherwise
     */
    public boolean isAuthenticated() {
        return accessToken != null && !accessToken.isEmpty();
    }

    // ============================================================================
    // DATABASE OPERATIONS
    // ============================================================================

    /**
     * Query a database table.
     * Returns all rows unless filtered.
     *
     * @param table the table name
     * @param responseType the class type to deserialize into
     * @param <T> the response type
     * @return the query result
     * @throws IOException if the request fails
     */
    public <T> T query(String table, Class<T> responseType) throws IOException {
        Request request = new Request.Builder()
            .url(supabaseUrl + "/rest/v1/" + table)
            .header("apikey", anonKey)
            .header("Authorization", "Bearer " + accessToken)
            .get()
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new IOException("Query failed: " + responseBody);
            }
            return gson.fromJson(responseBody, responseType);
        }
    }

    /**
     * Query a database table with filters.
     * Example filter: "id=eq.123" or "name=like.*john*"
     *
     * @param table the table name
     * @param filter PostgREST filter string
     * @param responseType the class type to deserialize into
     * @param <T> the response type
     * @return the query result
     * @throws IOException if the request fails
     */
    public <T> T queryWithFilter(String table, String filter, Class<T> responseType) throws IOException {
        Request request = new Request.Builder()
            .url(supabaseUrl + "/rest/v1/" + table + "?" + filter)
            .header("apikey", anonKey)
            .header("Authorization", "Bearer " + accessToken)
            .get()
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new IOException("Query failed: " + responseBody);
            }
            return gson.fromJson(responseBody, responseType);
        }
    }

    /**
     * Insert data into a database table.
     *
     * @param table the table name
     * @param data the data object to insert
     * @param responseType the class type to deserialize into
     * @param <T> the response type
     * @return the inserted row (with auto-generated fields like ID)
     * @throws IOException if the request fails
     */
    public <T> T insert(String table, Object data, Class<T> responseType) throws IOException {
        String json = gson.toJson(data);

        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
            .url(supabaseUrl + "/rest/v1/" + table)
            .header("apikey", anonKey)
            .header("Authorization", "Bearer " + accessToken)
            .header("Prefer", "return=representation")
            .post(body)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new IOException("Insert failed: " + responseBody);
            }
            return gson.fromJson(responseBody, responseType);
        }
    }

    /**
     * Update data in a database table.
     *
     * @param table the table name
     * @param filter PostgREST filter to select rows (e.g., "id=eq.123")
     * @param data the data object with fields to update
     * @param responseType the class type to deserialize into
     * @param <T> the response type
     * @return the updated row(s)
     * @throws IOException if the request fails
     */
    public <T> T update(String table, String filter, Object data, Class<T> responseType) throws IOException {
        String json = gson.toJson(data);

        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
            .url(supabaseUrl + "/rest/v1/" + table + "?" + filter)
            .header("apikey", anonKey)
            .header("Authorization", "Bearer " + accessToken)
            .header("Prefer", "return=representation")
            .patch(body)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new IOException("Update failed: " + responseBody);
            }
            return gson.fromJson(responseBody, responseType);
        }
    }

    /**
     * Delete data from a database table.
     *
     * @param table the table name
     * @param filter PostgREST filter to select rows (e.g., "id=eq.123")
     * @throws IOException if the request fails
     */
    public void delete(String table, String filter) throws IOException {
        Request request = new Request.Builder()
            .url(supabaseUrl + "/rest/v1/" + table + "?" + filter)
            .header("apikey", anonKey)
            .header("Authorization", "Bearer " + accessToken)
            .delete()
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                throw new IOException("Delete failed: " + responseBody);
            }
        }
    }

    // ============================================================================
    // GETTERS
    // ============================================================================

    /**
     * Get the current access token.
     *
     * @return the JWT access token, or null if not authenticated
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Set the access token manually (for testing or session restoration).
     *
     * @param accessToken the JWT access token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Shutdown the HTTP client and release resources.
     * Call this when the client is no longer needed to prevent memory leaks.
     */
    public void shutdown() {
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
    }
}
