package app.ui.view;

import interfaceadapter.simulated_trading.TradingController;
import interfaceadapter.simulated_trading.TradingState;
import interfaceadapter.simulated_trading.TradingViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.util.Map;

import entity.Position;

@SuppressWarnings({"checkstyle:ClassFanOutComplexity", "checkstyle:ClassDataAbstractionCoupling"})
public class TradingView extends JPanel implements ActionListener, PropertyChangeListener {

    @SuppressWarnings("checkstyle:VisibilityModifier")
    public final String viewName = "trading";
    private final TradingViewModel viewModel;
    private final TradingController controller;

    // UI Components
    private final JLabel availableCashLabel = new JLabel();
    private final JLabel tickerLabel = new JLabel("---");

    // Portfolio Summary Labels (8 Total Metrics)
    private final JLabel totalProfitLabel = new JLabel("0.00");
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    private final JLabel totalReturnLabel = new JLabel("0.00%");
    private final JLabel maxDrawdownLabel = new JLabel("0.00%");
    private final JLabel maxGainLabel = new JLabel("0.00%");
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    private final JLabel totalTradesLabel = new JLabel("0");
    private final JLabel winningTradesLabel = new JLabel("0");
    private final JLabel losingTradesLabel = new JLabel("0");
    private final JLabel winRateLabel = new JLabel("0.00%");

    // Wallet Table Components
    private final DefaultTableModel walletTableModel;
    private final JTable walletTable;

    private final JTextField amountField = new JTextField(10);
    private final JButton buyButton = new JButton(TradingViewModel.BUY_BUTTON_LABEL);
    private final JButton sellButton = new JButton(TradingViewModel.SELL_BUTTON_LABEL);
    private final PriceChartPanel chartPanel = new PriceChartPanel();

    private final JButton backButton = new JButton("Back");
    private final JButton orderHistoryButton = new JButton("View All Order History");

    private final Timer timer = new Timer(1000, this);

    @SuppressWarnings({"checkstyle:IllegalCatch", "checkstyle:RightCurly", "checkstyle:LineLength", "checkstyle:ReturnCount", "checkstyle:LambdaBodyLength", "checkstyle:LambdaParameterName", "checkstyle:FinalLocalVariable", "checkstyle:MagicNumber", "checkstyle:VariableDeclarationUsageDistance", "checkstyle:MultipleStringLiterals", "checkstyle:JavaNCSS", "checkstyle:ExecutableStatementCount"})
    public TradingView(TradingController controller, TradingViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        String[] columnNames = {"Name", "Type", "Amount(USD)", "Buying time", "Buying Price", "Current Price", "Unrealized Profit", "Unrealized Return Rate"};
        walletTableModel = new DefaultTableModel(columnNames, 0);
        walletTable = new JTable(walletTableModel);

        this.setLayout(new BorderLayout());

        // --- 1. TOP HEADER (Modified for Back Button) ---
        JPanel headerPanel = new JPanel(new BorderLayout());

        // Left Side: Cash
        JPanel cashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cashPanel.add(new JLabel("Available Virtual Money: "));
        cashPanel.add(availableCashLabel);

        // Center: Ticker
        tickerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JPanel tickerPanel = new JPanel();
        tickerPanel.add(tickerLabel);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightHeader.add(backButton);

        headerPanel.add(cashPanel, BorderLayout.WEST);
        headerPanel.add(tickerPanel, BorderLayout.SOUTH);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        this.add(headerPanel, BorderLayout.NORTH);

        // --- 2. RIGHT PANEL (Controls) ---
        this.add(createControlPanel(), BorderLayout.EAST);

        // --- 3. CENTER CONTAINER (Chart + Wallet Table) ---
        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));

        chartPanel.setPreferredSize(new Dimension(800, 450));
        centerContainer.add(chartPanel);

        JPanel walletSection = new JPanel(new BorderLayout());
        walletSection.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        walletSection.setBorder(new EmptyBorder(10, 5, 0, 5));

        // Wallet Header
        JPanel walletHeader = new JPanel(new BorderLayout());
        JLabel walletTitle = new JLabel("Wallet Holdings");
        walletTitle.setFont(new Font("Arial", Font.BOLD, 14));
        walletTitle.setBorder(new EmptyBorder(0, 5, 5, 0));

        // Style History Button
        orderHistoryButton.setForeground(Color.BLUE);
        orderHistoryButton.setBorderPainted(false);
        orderHistoryButton.setContentAreaFilled(false);
        orderHistoryButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        orderHistoryButton.setHorizontalAlignment(SwingConstants.RIGHT);

        walletHeader.add(walletTitle, BorderLayout.WEST);
        walletHeader.add(orderHistoryButton, BorderLayout.EAST);

        walletSection.add(walletHeader, BorderLayout.NORTH);

        JScrollPane walletScrollPane = new JScrollPane(walletTable);
        walletScrollPane.setPreferredSize(new Dimension(800, 150));
        walletSection.add(walletScrollPane, BorderLayout.CENTER);

        centerContainer.add(walletSection);
        this.add(centerContainer, BorderLayout.CENTER);

        // --- 4. BOTTOM PANEL (Summary Stats) ---
        this.add(createSummaryPanel(), BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            java.awt.Window currentWindow = SwingUtilities.getWindowAncestor(this);
            if (currentWindow != null) {
                currentWindow.dispose();
            }

            try {
                app.CompanyListMain.main(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to launch Company List: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // Simplified: delegate to controller, which knows session + userId
        orderHistoryButton.addActionListener(e -> {
            if (controller == null) {
                JOptionPane.showMessageDialog(this, "Simulation not fully initialized.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                controller.executeOpenPortfolioSummary();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to open summary: " + ex.getMessage());
            }
        });

        if (this.controller != null) {
            timer.start();
        }
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:LambdaParameterName", "checkstyle:FinalLocalVariable"})
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Amount(USD)"));
        panel.add(amountField);
        panel.add(Box.createVerticalStrut(10));

        buyButton.setBackground(new Color(0, 150, 0));
        buyButton.setForeground(Color.BLACK);
        buyButton.setOpaque(true);

        sellButton.setBackground(new Color(150, 0, 0));
        sellButton.setForeground(Color.BLACK);
        sellButton.setOpaque(true);

        buyButton.addActionListener(e -> handleTrade(true));
        sellButton.addActionListener(e -> handleTrade(false));

        panel.add(buyButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(sellButton);
        return panel;
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:FinalLocalVariable"})
    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Portfolio Summary"));

        summaryPanel.add(createStatPanel("Total Equity", totalProfitLabel));
        summaryPanel.add(createStatPanel("Total Return Rate", totalReturnLabel));

        summaryPanel.add(createStatPanel("Max Gain", maxGainLabel));
        summaryPanel.add(createStatPanel("Max Drawdown", maxDrawdownLabel));

        summaryPanel.add(createStatPanel("Total Trades", totalTradesLabel));
        summaryPanel.add(createStatPanel("Winning Trades", winningTradesLabel));

        summaryPanel.add(createStatPanel("Losing Trades", losingTradesLabel));
        summaryPanel.add(createStatPanel("Win Rate", winRateLabel));

        return summaryPanel;
    }

    @SuppressWarnings({"checkstyle:RegexpMultiline", "checkstyle:MagicNumber", "checkstyle:FinalLocalVariable"})
    private JPanel createStatPanel(String title, JLabel valueLabel) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.PLAIN, 10));
        titleLbl.setForeground(Color.GRAY);
        p.add(titleLbl, BorderLayout.NORTH);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }


    @SuppressWarnings({"checkstyle:LineLength", "checkstyle:IllegalCatch", "checkstyle:RightCurly", "checkstyle:FinalLocalVariable", "checkstyle:ReturnCount"})
    private void handleTrade(boolean isBuy) {
        if (controller == null) {
            JOptionPane.showMessageDialog(this, "Simulation not fully initialized. Please start from Setup View.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double amount = Double.parseDouble(amountField.getText());
            String priceStr = viewModel.getState().getCurrentPrice().replace("$", "").replace(",", "");
            double currentPrice = Double.parseDouble(priceStr);

            controller.executeTrade(viewModel.getState().getTicker(), amount, isBuy, currentPrice);
            amountField.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error in trade input or price data. Is simulation running?", "Trade Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (controller != null) {
            controller.executeTimerTick();
        }
    }

    @SuppressWarnings("checkstyle:FinalLocalVariable")
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        TradingState state = (TradingState) evt.getNewValue();

        if (state.getError() != null) {
            JOptionPane.showMessageDialog(this, state.getError());
        }

        availableCashLabel.setText(state.getAvailableCash());
        totalProfitLabel.setText(state.getTotalProfit());
        totalReturnLabel.setText(state.getTotalReturnRate());
        maxDrawdownLabel.setText(state.getMaxDrawdown());
        maxGainLabel.setText(state.getMaxGain());
        totalTradesLabel.setText(state.getTotalTrades());
        winningTradesLabel.setText(state.getWinningTrades());
        losingTradesLabel.setText(state.getLosingTrades());
        winRateLabel.setText(state.getWinRate());

        chartPanel.updateData(state.getChartData());
        tickerLabel.setText(state.getTicker());

        updateWalletTable(state.getPositions(), state.getCurrentPrice());
    }

    @SuppressWarnings({"checkstyle:CatchParameterName", "checkstyle:RightCurly", "checkstyle:MagicNumber", "checkstyle:ArrayTrailingComma", "checkstyle:Indentation", "checkstyle:AvoidInlineConditionals", "checkstyle:FinalLocalVariable", "checkstyle:LineLength"})
    private void updateWalletTable(Map<String, Position> positions, String currentPriceStr) {
        walletTableModel.setRowCount(0);

        try {
            double currentPrice = Double.parseDouble(currentPriceStr.replace("$", "").replace(",", ""));

            for (Map.Entry<String, Position> entry : positions.entrySet()) {
                Position p = entry.getValue();

                double totalCostBasis = p.getAvgPrice() * p.getQuantity();
                double unrealizedPnL = p.getUnrealizedPnL(currentPrice);
                double entryValue = p.getAvgPrice() * p.getQuantity();
                double returnRate = entryValue != 0 ? (unrealizedPnL / entryValue) : 0;

                String simplifiedTime = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                walletTableModel.addRow(new Object[]{
                        p.getTicker(),
                        p.isLong() ? "Buy" : "Sell",
                        String.format("%,.2f usd", totalCostBasis),
                        simplifiedTime,
                        String.format("%,.2f", p.getAvgPrice()),
                        String.format("%,.2f", currentPrice),
                        String.format("%,.2f usd", unrealizedPnL),
                        String.format("%.3f%%", returnRate * 100)
                });
            }
        } catch (NumberFormatException e) {
            // Price data hasn't loaded yet, ignore table update
        }
    }
}
