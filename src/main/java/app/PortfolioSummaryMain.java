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
import use_case.portfolio_statistics.PortfolioStatisticsInputData;
import use_case.portfolio_statistics.PortfolioStatisticsInteractor;
import use_case.portfolio_statistics.PortfolioStatisticsOutputData;
import use_case.session.SessionDataAccessInterface;

public class PortfolioSummaryMain {

    /**
     *  Instead of main(), we expose:
     *      PortfolioSummaryMain.show(userId, sessionDAO)
     *
     *  This allows any page (TradingView, CompanyPage, etc.)
     *  to open the Portfolio Summary page **without logging in again**.
     */
    public static void show(UUID userId, SessionDataAccessInterface sessionDAO) {

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

        // Fetch trades and calculate statistics
        dataaccess.SupabaseTradeDataAccessObject tradeDAO = new dataaccess.SupabaseTradeDataAccessObject();
        List<SimulatedTradeRecord> trades = tradeDAO.fetchTradesForUser(userId);

        PortfolioStatisticsInteractor statsInteractor = new PortfolioStatisticsInteractor();
        PortfolioStatisticsInputData statsInput = new PortfolioStatisticsInputData(trades);
        PortfolioStatisticsOutputData stats = statsInteractor.calculateStatistics(statsInput);

        contentPanel.add(new PortfolioSummaryCard(stats));
        contentPanel.add(new PortfolioOrderHistoryTable(userId, tradeDAO));

        // Attach components to frame
        background.add(contentPanel, BorderLayout.CENTER);
        frame.setContentPane(background);

        // Show the summary window
        frame.setVisible(true);
    }
}
