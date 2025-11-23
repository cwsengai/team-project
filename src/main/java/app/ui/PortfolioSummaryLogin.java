package app.ui;

import javax.swing.JOptionPane;

import data_access.InMemorySessionDataAccessObject;

public class PortfolioSummaryLogin {
    public static void loginOrShowDialog(InMemorySessionDataAccessObject userSessionDAO) {
        boolean loggedIn = false; // Simulate not logged in
        if (!loggedIn) {
            JOptionPane.showMessageDialog(null,
                    "No login page exists. Using a specific test user for this session.",
                    "Login Required", JOptionPane.INFORMATION_MESSAGE);

            // Use the specific user credentials provided
            String email = "fakeuser@example.com";
            String password = "FakePassword123!";
            try {
                String supabaseUrl = data_access.EnvConfig.getSupabaseUrl();
                String supabaseApiKey = data_access.EnvConfig.getSupabaseAnonKey();
                api.SupabaseAuthClient authClient = new api.SupabaseAuthClient(supabaseUrl, supabaseApiKey);
                org.json.JSONObject loginResult = authClient.signIn(email, password);
                String jwt = loginResult.optString("access_token", null);
                if (jwt != null) {
                    userSessionDAO.setJwtToken(jwt);
                } else {
                    String errorMsg = loginResult.optString("error", "Unknown error");
                    String errorDesc = loginResult.optString("error_description", loginResult.toString());
                    System.err.println("Supabase login failed: " + errorMsg + ", " + errorDesc);
                    JOptionPane.showMessageDialog(null,
                            "Supabase login failed: " + errorMsg + "\n" + errorDesc,
                            "Login Error", JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException("Failed to login as the specific test user. " + errorMsg + ": " + errorDesc);
                }
            } catch (java.io.IOException | RuntimeException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Exception during login: " + e.getMessage(),
                        "Login Exception", JOptionPane.ERROR_MESSAGE);
                throw new RuntimeException("Failed to login as the specific test user.", e);
            }
        }
    }
}
