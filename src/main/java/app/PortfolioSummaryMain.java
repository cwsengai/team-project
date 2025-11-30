package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Base64;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.json.JSONObject;

import app.ui.PortfolioOrderHistoryTable;
import app.ui.PortfolioSummaryCard;
import app.ui.PortfolioSummaryHeader;
import app.ui.PortfolioSummaryNavBar;
import usecase.session.SessionDataAccessInterface;

public class PortfolioSummaryMain {

    /**
     *  Instead of main(), we expose:
     *      PortfolioSummaryMain.show(userId, sessionDAO)
     *
     *  This allows any page (TradingView, CompanyPage, etc.)
     *  to open the Portfolio Summary page **without logging in again**.
     */
    public static void show(UUID userId, SessionDataAccessInterface sessionDAO) {

        // Debug logs
        System.out.println("PortfolioSummary.show called with userId: " + userId);
        String jwt = sessionDAO.getJwtToken();
        if (jwt != null) {
            try {
                String[] parts = jwt.split("\\.");
                if (parts.length >= 2) {
                    String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
                    JSONObject payload = new JSONObject(payloadJson);
                    String email = payload.optString("email", "unknown");
                    String sub = payload.optString("sub", "unknown");
                    System.out.println("User email from JWT: " + email);
                    System.out.println("User sub (ID) from JWT: " + sub);
                    System.out.println("Session userId matches JWT sub: " + userId.toString().equals(sub));
                }
            } catch (Exception e) {
                System.out.println("Error decoding JWT: " + e.getMessage());
            }
        } else {
            System.out.println("No JWT token in session");
        }

        // === Build the Portfolio Summary window ===
        JFrame frame = new JFrame("Portfolio Summary");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);

        // Main background panel
        JPanel background = new JPanel();
        background.setBackground(new Color(0xF7, 0xF7, 0xF7));
        background.setLayout(new BorderLayout());

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // Add UI components
        contentPanel.add(new PortfolioSummaryNavBar(frame));
        contentPanel.add(new PortfolioSummaryHeader());
        contentPanel.add(new PortfolioSummaryCard());

        // Order History Table (needs the correct userId)
        dataaccess.SupabaseTradeDataAccessObject tradeDAO = new dataaccess.SupabaseTradeDataAccessObject();
        try {
            var trades = tradeDAO.fetchTradesForUser(userId);
            System.out.println("Fetched " + trades.size() + " trades for user " + userId);
        } catch (Exception e) {
            System.out.println("Error fetching trades: " + e.getMessage());
        }
        contentPanel.add(new PortfolioOrderHistoryTable(userId, tradeDAO));

        // Attach components to frame
        background.add(contentPanel, BorderLayout.CENTER);
        frame.setContentPane(background);

        // Show the summary window
        frame.setVisible(true);
    }
}
