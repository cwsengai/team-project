package app;

import javax.swing.SwingUtilities;

import app.ui.PortfolioOrderHistoryTable;
import app.ui.PortfolioSummaryCard;
import app.ui.PortfolioSummaryHeader;
import app.ui.PortfolioSummaryLogin;
import app.ui.PortfolioSummaryNavBar;

public class PortfolioSummaryMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // === Session Management ===
            data_access.InMemorySessionDataAccessObject userSessionDAO = new data_access.InMemorySessionDataAccessObject();

            // === Login (refactored) ===
            PortfolioSummaryLogin.loginOrShowDialog(userSessionDAO);

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

            // Add UI components
            contentPanel.add(new PortfolioSummaryNavBar(frame));
            contentPanel.add(new PortfolioSummaryHeader());
            contentPanel.add(new PortfolioSummaryCard());
            contentPanel.add(new PortfolioOrderHistoryTable());

            // Add contentPanel to background (centered)
            background.add(contentPanel, java.awt.BorderLayout.CENTER);
            frame.setContentPane(background);
            frame.setVisible(true);
        });
    }
}
