package data_access.client;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import data_access.config.EnvConfig;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Client for interacting with Supabase REST API and authentication.
 * Handles JWT-based authentication and database operations via PostgREST.
 */
public class SupabaseClient implements AutoCloseable {
    private static final String DEFAULT_SUPABASE_URL = EnvConfig.getSupabaseUrl();
    private static final String DEFAULT_SUPABASE_ANON_KEY = EnvConfig.getSupabaseAnonKey();
    private static final String SERVICE_ROLE_KEY = EnvConfig.getSupabaseServiceRoleKey();
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");

    private final String supabaseUrl;
    private final String anonKey;
    private final boolean useServiceRole;
    private final OkHttpClient httpClient;
    private final Gson gson;
    private String accessToken;

    /**
     * Creates a new Supabase client with default settings.
     */
    public SupabaseClient() {
        this(DEFAULT_SUPABASE_URL, DEFAULT_SUPABASE_ANON_KEY, false);
    }

    /**
     * Creates a new Supabase client with service role (admin) permissions.
     * Use this for operations that bypass RLS policies.
     *
     * @param useServiceRole if true, uses service role key instead of anon key
     */
    public SupabaseClient(boolean useServiceRole) {
        this(DEFAULT_SUPABASE_URL, useServiceRole ? SERVICE_ROLE_KEY : DEFAULT_SUPABASE_ANON_KEY, useServiceRole);
    }

    /**
     * Creates a new Supabase client with custom URL and API key.
     * Use this constructor to override the default Supabase project settings.
     *
     * @param supabaseUrl the Supabase project URL
     * @param anonKey the anonymous API key or service role key
     * @param useServiceRole if true, treats anonKey as service role key
     */
    public SupabaseClient(String supabaseUrl, String anonKey, boolean useServiceRole) {
        this.supabaseUrl = supabaseUrl;
        this.anonKey = anonKey;
        this.useServiceRole = useServiceRole;
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
        // TODO: Configure connection pooling for better performance
        // TODO: Add retry logic for transient network failures
        this.gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                @Override
                public void write(JsonWriter out, LocalDateTime value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                    } else {
                        out.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    }
                }

                @Override
                public LocalDateTime read(JsonReader in) throws IOException {
                    if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    String timestamp = in.nextString();
                    return java.time.OffsetDateTime.parse(timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        .toLocalDateTime();
                }
            })
            .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                @Override
                public void write(JsonWriter out, LocalDate value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                    } else {
                        out.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
                    }
                }

                @Override
                public LocalDate read(JsonReader in) throws IOException {
                    if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    return LocalDate.parse(in.nextString(), DateTimeFormatter.ISO_LOCAL_DATE);
                }
            })
            .create();
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
        // TODO: Add input validation and sanitization to prevent injection attacks
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
            String responseBody = Objects.requireNonNull(response.body(), "Response body is null").string();
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
        // TODO: Add input validation and sanitization to prevent injection attacks
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
            String responseBody = Objects.requireNonNull(response.body(), "Response body is null").string();
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
     */
    public void signOut() {
        this.accessToken = null;
        // TODO: Implement proper sign out via Supabase auth endpoint
        // TODO: Invalidate refresh token on server side
    }

    /**
     * Check if a user is currently authenticated.
     *
     * @return true if authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        // TODO: Check token expiration, not just existence
        // TODO: Implement automatic token refresh using refresh_token
        return accessToken != null && !accessToken.isEmpty();
    }

    /**
     * Get the authorization token for API requests.
     *
     * @return service role key if using service role, otherwise user's access token
     */
    private String getAuthToken() {
        if (useServiceRole) {
            return anonKey;
        }
        return accessToken != null ? accessToken : anonKey;
    }

    // ============================================================================
    // DATABASE OPERATIONS
    // ============================================================================

    /**
     * Query a database table. Returns all rows unless filtered.
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
            .header("Authorization", "Bearer " + getAuthToken())
            .get()
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body(), "Response body is null").string();
            if (!response.isSuccessful()) {
                throw new IOException("Query failed: " + responseBody);
            }
            return gson.fromJson(responseBody, responseType);
        }
    }

    /**
     * Query a database table with filters (e.g., "id=eq.123" or "name=like.*john*").
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
            .header("Authorization", "Bearer " + getAuthToken())
            .get()
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body(), "Response body is null").string();
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
     * @return the inserted row with auto-generated fields
     * @throws IOException if the request fails
     */
    public <T> T insert(String table, Object data, Class<T> responseType) throws IOException {
        String json = gson.toJson(data);

        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);

        Request request = new Request.Builder()
            .url(supabaseUrl + "/rest/v1/" + table)
            .header("apikey", anonKey)
            .header("Authorization", "Bearer " + getAuthToken())
            .header("Prefer", "return=representation")
            .post(body)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body(), "Response body is null").string();
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
     * @param filter PostgREST filter (e.g., "id=eq.123")
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
            .header("Authorization", "Bearer " + getAuthToken())
            .header("Prefer", "return=representation")
            .patch(body)
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body(), "Response body is null").string();
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
     * @param filter PostgREST filter (e.g., "id=eq.123")
     * @throws IOException if the request fails
     */
    public void delete(String table, String filter) throws IOException {
        Request request = new Request.Builder()
            .url(supabaseUrl + "/rest/v1/" + table + "?" + filter)
            .header("apikey", anonKey)
            .header("Authorization", "Bearer " + getAuthToken())
            .delete()
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = Objects.requireNonNull(response.body(), "Response body is null").string();
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
     * @return JWT access token, or null if not authenticated
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Shutdown the HTTP client and release resources.
     * Implements AutoCloseable for use with try-with-resources.
     */
    @Override
    public void close() {
        shutdown();
    }

    /**
     * Shutdown the HTTP client and release resources.
     */
    public void shutdown() {
        if (httpClient != null) {
            try (var executorService = httpClient.dispatcher().executorService()) {
                executorService.shutdown();
                httpClient.connectionPool().evictAll();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                httpClient.dispatcher().executorService().shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
