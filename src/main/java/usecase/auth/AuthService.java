package usecase.auth;

import org.json.JSONObject;

import api.SupabaseAuthClient;
import dataaccess.EnvConfig;

public class AuthService {

    private final SupabaseAuthClient auth;

    public AuthService() {
        this.auth = new SupabaseAuthClient(
                EnvConfig.getSupabaseUrl(),
                EnvConfig.getSupabaseAnonKey()
        );
    }

    public String login(String email, String password) {
        try {
            JSONObject result = auth.signIn(email, password);
            return result.optString("access_token", null);
        } catch (Exception e) {
            System.err.println("Login failed: " + e.getMessage());
            return null; // Login failed
        }
    }

    public String signup(String email, String password) {
        try {
            JSONObject result = auth.signUp(email, password);
            return result.optString("access_token", null);
        } catch (Exception e) {
            System.err.println("Signup failed: " + e.getMessage());
            return null; // Signup failed
        }
    }
}
