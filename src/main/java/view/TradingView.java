package view;

import interface_adapter.simulated_trading.TradingController;
import interface_adapter.simulated_trading.TradingState;
import interface_adapter.simulated_trading.TradingViewModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import entity.Position;

public class TradingView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "trading";
    private final TradingViewModel viewModel;
    private final TradingController controller;

    // UI Components
    private final JLabel availableCashLabel = new JLabel(); // Top Cash
    private final JLabel tickerLabel = new JLabel("AAPL"); // Display the stock name (simplified)
    private final PriceChartPanel chartPanel = new PriceChartPanel();

    // Portfolio Summary Labels
    private final JLabel totalProfitLabel = new JLabel();
    private final JLabel totalReturnLabel = new JLabel();
    private final JLabel maxDrawdownLabel = new JLabel();
    private final JLabel maxGainLabel = new JLabel(); // Missing from previous code
    private final JLabel totalTradesLabel = new JLabel(); // Missing
    private final JLabel winRateLabel = new JLabel(); // Missing

    private final JTextField amountField = new JTextField(10);
    private final JButton buyButton = new JButton(TradingViewModel.BUY_BUTTON_LABEL);
    private final JButton sellButton = new JButton(TradingViewModel.SELL_BUTTON_LABEL);

    // Wallet Table
    private final DefaultTableModel walletTableModel;
    private final JTable walletTable;

    private final Timer timer = new Timer(1000, this);

    public TradingView(TradingController controller, TradingViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        // --- Wallet Table Setup ---
        String[] columnNames = {"Name", "Type", "Quantity", "Avg Price", "Current Price", "PnL", "Return"};
        walletTableModel = new DefaultTableModel(columnNames, 0);
        walletTable = new JTable(walletTableModel);

        this.setLayout(new BorderLayout());

        // --- 1. TOP & CHART ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        JPanel cashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cashPanel.add(new JLabel("Available Virtual Money: "));
        cashPanel.add(availableCashLabel);

        JPanel tickerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        tickerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        tickerPanel.add(tickerLabel);

        headerPanel.add(cashPanel, BorderLayout.NORTH);
        headerPanel.add(tickerPanel, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        this.add(chartPanel, BorderLayout.CENTER);

        // --- 2. RIGHT PANEL (Controls) ---
        JPanel rightPanel = createControlPanel();
        this.add(rightPanel, BorderLayout.EAST);

        // --- 3. BOTTOM PANEL (Wallet & Summary) ---
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));

        // Wallet Table
        bottomContainer.add(new JScrollPane(walletTable));

        // Summary
        bottomContainer.add(createSummaryPanel());

        this.add(bottomContainer, BorderLayout.SOUTH);

        timer.start();
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Amount(USD)"));
        panel.add(amountField);
        panel.add(Box.createVerticalStrut(10));

        buyButton.setBackground(Color.GREEN);
        sellButton.setBackground(Color.RED);

        buyButton.addActionListener(e -> handleTrade(true));
        sellButton.addActionListener(e -> handleTrade(false));

        panel.add(buyButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(sellButton);
        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(2, 4, 10, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Portfolio Summary"));

        // Row 1
        summaryPanel.add(createStatPanel("Total Profit", totalProfitLabel));
        summaryPanel.add(createStatPanel("Total Return Rate", totalReturnLabel));
        summaryPanel.add(createStatPanel("Max Gain", maxGainLabel));
        summaryPanel.add(createStatPanel("Total Trades", totalTradesLabel));

        // Row 2 (Placeholders for now, or you can add Win Rate etc.)
        summaryPanel.add(createStatPanel("Max Drawdown", maxDrawdownLabel));
        summaryPanel.add(createStatPanel("Win Rate", winRateLabel));

        return summaryPanel;
    }

    private JPanel createStatPanel(String title, JLabel valueLabel) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        valueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        p.add(titleLbl, BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }

    private void handleTrade(boolean isBuy) {
        // ... (Trade execution logic remains the same)
        try {
            double amount = Double.parseDouble(amountField.getText());
            String priceStr = viewModel.getState().getCurrentPrice().replace("$", "").replace(",", "");
            double currentPrice = Double.parseDouble(priceStr);

            controller.executeTrade(viewModel.getState().getTicker(), amount, isBuy, currentPrice);
            amountField.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error in trade input or price data. Is simulation running?");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        controller.executeTimerTick();
    }

    /**
     * Property Change Listener (Updates all UI labels and tables)
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        TradingState state = (TradingState) evt.getNewValue();

        if (state.getError() != null) {
            JOptionPane.showMessageDialog(this, state.getError());
        }

        // Update Labels
        availableCashLabel.setText(state.getAvailableCash());
        totalProfitLabel.setText(state.getTotalProfit());
        totalReturnLabel.setText(state.getTotalReturnRate());
        maxDrawdownLabel.setText(state.getMaxDrawdown());
        maxGainLabel.setText(state.getMaxGain()); // New label update
        totalTradesLabel.setText(state.getTotalTrades()); // New label update
        winRateLabel.setText(state.getWinRate()); // New label update

        // Update Chart
        chartPanel.updateData(state.getChartData());

        // Update Wallet Table (This is complex, often requires custom table model)
        updateWalletTable(state.getPositions(), state.getCurrentPrice());
    }

    private void updateWalletTable(Map<String, Position> positions, String currentPriceStr) {
        // Simple implementation to show data:
        walletTableModel.setRowCount(0); // Clear old rows

        try {
            double currentPrice = Double.parseDouble(currentPriceStr.replace("$", "").replace(",", ""));

            for (Map.Entry<String, Position> entry : positions.entrySet()) {
                Position p = entry.getValue();
                double unrealizedPnL = p.getUnrealizedPnL(currentPrice);
                double entryValue = p.getAvgPrice() * p.getQuantity();
                double currentValue = currentPrice * p.getQuantity();
                double returnRate = entryValue != 0 ? (unrealizedPnL / entryValue) : 0;

                walletTableModel.addRow(new Object[]{
                        p.getTicker(),
                        p.isLong() ? "Buy/Long" : "Sell/Short",
                        p.getQuantity(),
                        String.format("%.2f", p.getAvgPrice()),
                        String.format("%.2f", currentPrice),
                        String.format("%,.2f", unrealizedPnL),
                        String.format("%.2f%%", returnRate * 100)
                });
            }
        } catch (NumberFormatException e) {
            // Price hasn't loaded yet, ignore table update
        }
    }
}