package app.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import interfaceadapter.view_model.PortfolioSummaryViewModel;

@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class PortfolioSummaryCard extends JPanel {
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("$#,##0.00");
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#0.00'%'");

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:FinalLocalVariable", "checkstyle:MultipleStringLiterals", "checkstyle:JavaNCSS", "checkstyle:ExecutableStatementCount"})
    public PortfolioSummaryCard(PortfolioStatisticsOutputData stats) {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));

        // Card container
        final JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(0xF0, 0xF0, 0xF0));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0, 0xE0, 0xE0), 1, true),
            BorderFactory.createEmptyBorder(24, 32, 24, 32)
        ));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setOpaque(true);

        // Title pill
        final JLabel title = new JLabel("Portfolio Summary", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(0x1A, 0x73, 0xE8));
        title.setOpaque(true);
        title.setBackground(new Color(0xE8, 0xF0, 0xFE));
        title.setBorder(BorderFactory.createEmptyBorder(6, 24, 6, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setMaximumSize(new Dimension(240, 36));
        card.add(title);
        card.add(Box.createVerticalStrut(16));

        // Trading Span
        final JLabel tradingSpanLabel = new JLabel("Trading Span:", SwingConstants.CENTER);
        tradingSpanLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tradingSpanLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(tradingSpanLabel);
        final String tradingSpanString;
        if (stats.earliest == null || stats.latest == null) {
            tradingSpanString = "No trades";
        }
        else {
            tradingSpanString = stats.earliest.toLocalDate() + " to " + stats.latest.toLocalDate();
        }
        final JLabel tradingSpanValue = new JLabel(tradingSpanString, SwingConstants.CENTER);
        tradingSpanValue.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tradingSpanValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(tradingSpanValue);
        card.add(Box.createVerticalStrut(16));

        // Metrics columns
        final JPanel metricsPanel = new JPanel();
        metricsPanel.setOpaque(false);
        metricsPanel.setLayout(new GridLayout(1, 2, 32, 0));

        // Left column
        final JPanel leftCol = new JPanel();
        leftCol.setOpaque(false);
        leftCol.setLayout(new GridLayout(4, 2, 8, 8));
        leftCol.add(new JLabel("Total Profit:"));
        leftCol.add(rightAlignLabel(CURRENCY_FORMAT.format(stats.totalProfit)));
        leftCol.add(new JLabel("Total Return Rate:"));
        leftCol.add(rightAlignLabel(PERCENT_FORMAT.format(stats.totalReturnRate)));
        leftCol.add(new JLabel("Max Gain:"));
        leftCol.add(rightAlignLabel(CURRENCY_FORMAT.format(stats.maxGain)));
        leftCol.add(new JLabel("Max Drawdown:"));
        leftCol.add(rightAlignLabel(CURRENCY_FORMAT.format(stats.maxDrawdown)));

        // Right column
        final JPanel rightCol = new JPanel();
        rightCol.setOpaque(false);
        rightCol.setLayout(new GridLayout(4, 2, 8, 8));
        rightCol.add(new JLabel("Total Trades#:"));
        rightCol.add(rightAlignLabel(String.valueOf(stats.tradeCount)));
        rightCol.add(new JLabel("Winning trades#:"));
        rightCol.add(rightAlignLabel(String.valueOf(stats.winningTrades)));
        rightCol.add(new JLabel("Losing trades#:"));
        rightCol.add(rightAlignLabel(String.valueOf(stats.losingTrades)));
        rightCol.add(new JLabel("Win Rate:"));
        rightCol.add(rightAlignLabel(PERCENT_FORMAT.format(stats.winRate)));

        metricsPanel.add(leftCol);
        metricsPanel.add(rightCol);
        card.add(metricsPanel);

        add(card, BorderLayout.CENTER);
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:FinalLocalVariable"})
    private JLabel rightAlignLabel(String text) {
        final JLabel label = new JLabel(text, SwingConstants.RIGHT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return label;
    }
}
