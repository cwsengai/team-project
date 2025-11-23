package app;

import javax.swing.SwingUtilities;

// Import your dependencies here as needed
// import ...

public class PortfolioSummaryMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // === Session Management ===
            data_access.InMemorySessionDataAccessObject userSessionDAO = new data_access.InMemorySessionDataAccessObject();

            // === Check login (simulate not logged in) ===
            boolean loggedIn = false; // Simulate not logged in
            if (!loggedIn) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "No login page exists. Using a specific test user for this session.",
                        "Login Required", javax.swing.JOptionPane.INFORMATION_MESSAGE);

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
                        javax.swing.JOptionPane.showMessageDialog(null,
                                "Supabase login failed: " + errorMsg + "\n" + errorDesc,
                                "Login Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        throw new RuntimeException("Failed to login as the specific test user. " + errorMsg + ": " + errorDesc);
                    }
                } catch (java.io.IOException | RuntimeException e) {
                    e.printStackTrace();
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Exception during login: " + e.getMessage(),
                            "Login Exception", javax.swing.JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException("Failed to login as the specific test user.", e);
                }
            }

            // === Show the portfolio summary page (main layout and background) ===
            javax.swing.JFrame frame = new javax.swing.JFrame("Portfolio Summary");
            frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(900, 700);
            frame.setLocationRelativeTo(null);

            // Main background panel with light grey color
            javax.swing.JPanel background = new javax.swing.JPanel();
            background.setBackground(new java.awt.Color(0xF7, 0xF7, 0xF7));
            background.setLayout(new java.awt.BorderLayout());

            // Main content panel (vertical box, centered, with padding)
            javax.swing.JPanel contentPanel = new javax.swing.JPanel();
            contentPanel.setOpaque(false);
            contentPanel.setLayout(new javax.swing.BoxLayout(contentPanel, javax.swing.BoxLayout.Y_AXIS));
            contentPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(32, 32, 32, 32));

            // Add contentPanel to background (centered)
            background.add(contentPanel, java.awt.BorderLayout.CENTER);
            frame.setContentPane(background);
            frame.setVisible(true);
        });
    }
}
