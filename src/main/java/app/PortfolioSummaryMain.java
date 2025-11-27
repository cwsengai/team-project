
package app;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import app.ui.LoginPage;
import app.ui.PortfolioOrderHistoryTable;
import app.ui.PortfolioSummaryCard;
import app.ui.PortfolioSummaryHeader;
import app.ui.PortfolioSummaryNavBar;

public class PortfolioSummaryMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // === Session Management ===
            dataaccess.InMemorySessionDataAccessObject userSessionDAO = new dataaccess.InMemorySessionDataAccessObject();

            // REAL LOGIN

            LoginPage loginWindow = new LoginPage(null, userSessionDAO);
            loginWindow.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
            loginWindow.setVisible(true); // Now blocks until login dialog is closed

            // If user closed without login:
            if (!loginWindow.wasSuccessful()) {
                System.out.println("Login cancelled or failed.");
                return; // do NOT exit entire app
            }

            // === Show the portfolio summary page (main layout and background) ===
            JFrame frame = new JFrame("Portfolio Summary");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(900, 700);
            frame.setLocationRelativeTo(null);

            // Main background panel with light grey color
            JPanel background = new JPanel();
            background.setBackground(new Color(0xF7, 0xF7, 0xF7));
            background.setLayout(new BorderLayout());

            // Main content panel (vertical box, centered, with padding)
            JPanel contentPanel = new JPanel();
            contentPanel.setOpaque(false);
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

            // Add UI components
            contentPanel.add(new PortfolioSummaryNavBar(frame));
            contentPanel.add(new PortfolioSummaryHeader());
            contentPanel.add(new PortfolioSummaryCard());
            // Pass userId and DAO to PortfolioOrderHistoryTable
            dataaccess.SupabaseTradeDataAccessObject tradeDAO = new dataaccess.SupabaseTradeDataAccessObject();
            java.util.UUID userId = userSessionDAO.getCurrentUserId();
            contentPanel.add(new PortfolioOrderHistoryTable(userId, tradeDAO));

            // Add contentPanel to background (centered)
            background.add(contentPanel, BorderLayout.CENTER);
            frame.setContentPane(background);
            frame.setVisible(true);
        });
    }
}
