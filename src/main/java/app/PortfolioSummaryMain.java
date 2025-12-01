package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import app.ui.PortfolioOrderHistoryTable;
import app.ui.PortfolioSummaryCard;
import app.ui.PortfolioSummaryHeader;
import app.ui.PortfolioSummaryNavBar;
import entity.SimulatedTradeRecord;
import usecase.portfolio_statistics.PortfolioStatisticsInputData;
import usecase.portfolio_statistics.PortfolioStatisticsInteractor;
import usecase.portfolio_statistics.PortfolioStatisticsOutputData;
import usecase.session.SessionDataAccessInterface;

public class PortfolioSummaryMain {

    /**
     * Displays the Portfolio Summary window for the given user. This method
     * is intended to be called from other application pages (such as TradingView
     * or CompanyPage) without requiring the user to log in again.
     *
     * <p>The window includes navigation, summary statistics, and trade history
     * calculated from the user's stored simulated trading records and initial
     * portfolio balance.</p>
     *
     * @param userId the unique identifier of the authenticated user
     * @param sessionDAO the session data access object used for session validation
     */
    public static void show(UUID userId, SessionDataAccessInterface sessionDAO) {

        // === Build the Portfolio Summary window ===
        final JFrame frame = new JFrame("Portfolio Summary");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);

        // Main background panel
        final JPanel background = new JPanel();
        background.setBackground(new Color(0xF7, 0xF7, 0xF7));
        background.setLayout(new BorderLayout());

        // Main content panel
        final JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(32, 32, 32, 32));

        // Add UI components
        contentPanel.add(new PortfolioSummaryNavBar(frame));
        contentPanel.add(new PortfolioSummaryHeader());

        // Fetch trades and calculate statistics
        final dataaccess.SupabaseTradeDataAccessObject tradeDAO = new dataaccess.SupabaseTradeDataAccessObject();
        final List<SimulatedTradeRecord> trades = tradeDAO.fetchTradesForUser(userId);

        // Fetch initial balance from database
        final dataaccess.SupabasePortfolioDataAccessObject portfolioDAO =
                new dataaccess.SupabasePortfolioDataAccessObject();
        final double initialBalance = portfolioDAO.getInitialBalance(userId);

        final PortfolioStatisticsInteractor statsInteractor = new PortfolioStatisticsInteractor();
        final PortfolioStatisticsInputData statsInput = new PortfolioStatisticsInputData(trades, initialBalance);
        final PortfolioStatisticsOutputData stats = statsInteractor.calculateStatistics(statsInput);

        contentPanel.add(new PortfolioSummaryCard(stats));
        contentPanel.add(new PortfolioOrderHistoryTable(userId, tradeDAO));

        // Attach components to frame
        background.add(contentPanel, BorderLayout.CENTER);
        frame.setContentPane(background);

        // Show the summary window
        frame.setVisible(true);
    }
}
