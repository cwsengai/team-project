package usecase.auth;

import api.SupabaseAuthClient;
import dataaccess.EnvConfig;
import org.json.JSONObject;

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
            e.printStackTrace();
            return null; // Login failed
        }
    }

    public String signup(String email, String password) {
        try {
            JSONObject result = auth.signUp(email, password);
            return result.optString("access_token", null);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Signup failed
        }
    }
}
