package usecase.auth;

import java.io.IOException;

import org.json.JSONObject;

import api.SupabaseAuthClient;
import dataaccess.EnvConfig;

/**
 * Service class that handles user authentication using Supabase.
 */
public class AuthService {

    private final SupabaseAuthClient auth;

    /**
     * Constructs an AuthService with Supabase client using environment config.
     */
    public AuthService() {
        this.auth = new SupabaseAuthClient(
                EnvConfig.getSupabaseUrl(),
                EnvConfig.getSupabaseAnonKey()
        );
    }

    /**
     * Attempts to log in a user with the given email and password.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return the access token if login succeeds; null otherwise
     */
    public String login(String email, String password) {
        try {
            JSONObject result = auth.signIn(email, password);
            return result.optString("access_token", null);
        }
        catch (IOException exception) {
            // Login failed
            System.err.println("AuthService.login error: " + exception.getMessage());
            for (StackTraceElement ste : exception.getStackTrace()) {
                System.err.println("    at " + ste.toString());
            }
            return null;
        }
    }

    /**
     * Attempts to sign up a user with the given email and password.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return the access token if signup succeeds; null otherwise
     */
    public String signup(String email, String password) {
        try {
            JSONObject result = auth.signUp(email, password);
            return result.optString("access_token", null);
        }
        catch (IOException exception) {
            // Signup failed
            System.err.println("AuthService.signup error: " + exception.getMessage());
            for (StackTraceElement ste : exception.getStackTrace()) {
                System.err.println("    at " + ste.toString());
            }
            return null;
        }
    }
}
