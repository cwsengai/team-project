package app;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import app.ui.LoginPage;
import app.ui.PortfolioOrderHistoryTable;
import app.ui.PortfolioSummaryCard;
import app.ui.PortfolioSummaryHeader;
import app.ui.PortfolioSummaryNavBar;
import use_case.session.SessionDataAccessInterface;

public class PortfolioSummaryMain {

    /**
     * Opens the Portfolio Summary page.
     * If user is NOT logged in, shows login first.
     * Uses the SAME session provided by caller.
     */
    public static void open(SessionDataAccessInterface sessionDAO) {

        // --------------------------------------------------------------------
        // 1. If NOT LOGGED IN → open login window
        // --------------------------------------------------------------------
        if (sessionDAO.getJwtToken() == null) {
            LoginPage loginWindow = new LoginPage(null, sessionDAO);
            loginWindow.setVisible(true);

            if (!loginWindow.wasSuccessful()) {
                System.out.println("Login cancelled or failed.");
                return;
            }
        }

        // --------------------------------------------------------------------
        // 2. User is now logged in → build and show Portfolio Summary page
        // --------------------------------------------------------------------
        JFrame frame = new JFrame("Portfolio Summary");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);

        JPanel background = new JPanel();
        background.setBackground(new Color(0xF7, 0xF7, 0xF7));
        background.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // Add UI components
        contentPanel.add(new PortfolioSummaryNavBar(frame));
        contentPanel.add(new PortfolioSummaryHeader());
        contentPanel.add(new PortfolioSummaryCard());

        // Real user ID from session
        java.util.UUID userId = sessionDAO.getCurrentUserId();
        data_access.SupabaseTradeDataAccessObject tradeDAO =
                new data_access.SupabaseTradeDataAccessObject();

        contentPanel.add(new PortfolioOrderHistoryTable(userId, tradeDAO));
        background.add(contentPanel, BorderLayout.CENTER);

        frame.setContentPane(background);
        frame.setVisible(true);
    }
}
