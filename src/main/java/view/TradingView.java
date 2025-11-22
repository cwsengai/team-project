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

    private final JLabel availableCashLabel = new JLabel();
    private final JLabel tickerLabel = new JLabel("---");

    private final JLabel totalProfitLabel = new JLabel("0.00");
    private final JLabel totalReturnLabel = new JLabel("0.00%");
    private final JLabel maxDrawdownLabel = new JLabel("0.00%");
    private final JLabel maxGainLabel = new JLabel("0.00%");
    private final JLabel totalTradesLabel = new JLabel("0");
    private final JLabel winRateLabel = new JLabel("0.00%");

    private final DefaultTableModel walletTableModel;
    private final JTable walletTable;

    private final JTextField amountField = new JTextField(10);
    private final JButton buyButton = new JButton(TradingViewModel.BUY_BUTTON_LABEL);
    private final JButton sellButton = new JButton(TradingViewModel.SELL_BUTTON_LABEL);
    private final PriceChartPanel chartPanel = new PriceChartPanel();

    private final Timer timer = new Timer(1000, this);

    public TradingView(TradingController controller, TradingViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        String[] columnNames = {"Name", "Type", "Qty", "Avg Price", "Current Price", "PnL", "Return"};
        walletTableModel = new DefaultTableModel(columnNames, 0);
        walletTable = new JTable(walletTableModel);

        this.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        JPanel cashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cashPanel.add(new JLabel("Available Virtual Money: "));
        cashPanel.add(availableCashLabel);

        tickerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JPanel tickerPanel = new JPanel();
        tickerPanel.add(tickerLabel);

        headerPanel.add(cashPanel, BorderLayout.NORTH);
        headerPanel.add(tickerPanel, BorderLayout.SOUTH);
        this.add(headerPanel, BorderLayout.NORTH);

        this.add(createControlPanel(), BorderLayout.EAST);

        JPanel centerContainer = new JPanel();
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));

        chartPanel.setPreferredSize(new Dimension(800, 450));
        centerContainer.add(chartPanel);

        JScrollPane walletScrollPane = new JScrollPane(walletTable);
        walletScrollPane.setPreferredSize(new Dimension(800, 150));
        walletScrollPane.setBorder(BorderFactory.createTitledBorder("Wallet Holdings"));
        centerContainer.add(walletScrollPane);

        this.add(centerContainer, BorderLayout.CENTER);

        this.add(createSummaryPanel(), BorderLayout.SOUTH);

        if (this.controller != null) {
            timer.start();
        }
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Amount(USD)"));
        panel.add(amountField);
        panel.add(Box.createVerticalStrut(10));

        buyButton.setBackground(new Color(0, 150, 0));
        buyButton.setForeground(Color.WHITE);
        buyButton.setOpaque(true);
        sellButton.setBackground(new Color(150, 0, 0));
        sellButton.setForeground(Color.WHITE);
        sellButton.setOpaque(true);

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

        summaryPanel.add(createStatPanel("Total Equity", totalProfitLabel));
        summaryPanel.add(createStatPanel("Total Return Rate", totalReturnLabel));
        summaryPanel.add(createStatPanel("Max Gain", maxGainLabel));
        summaryPanel.add(createStatPanel("Total Trades", totalTradesLabel));

        summaryPanel.add(createStatPanel("Max Drawdown", maxDrawdownLabel));
        summaryPanel.add(createStatPanel("Win Rate", winRateLabel));

        return summaryPanel;
    }

    private JPanel createStatPanel(String title, JLabel valueLabel) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel titleLbl = new JLabel(title);
        titleLbl.setForeground(Color.GRAY);
        p.add(titleLbl, BorderLayout.NORTH);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }


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
        winRateLabel.setText(state.getWinRate());

        chartPanel.updateData(state.getChartData());
        tickerLabel.setText(state.getTicker());

        updateWalletTable(state.getPositions(), state.getCurrentPrice());
    }

    private void updateWalletTable(Map<String, Position> positions, String currentPriceStr) {
        walletTableModel.setRowCount(0);

        try {
            double currentPrice = Double.parseDouble(currentPriceStr.replace("$", "").replace(",", ""));

            for (Map.Entry<String, Position> entry : positions.entrySet()) {
                Position p = entry.getValue();
                double unrealizedPnL = p.getUnrealizedPnL(currentPrice);
                double entryValue = p.getAvgPrice() * p.getQuantity();
                double returnRate = entryValue != 0 ? (unrealizedPnL / entryValue) : 0;

                walletTableModel.addRow(new Object[]{
                        p.getTicker(),
                        p.isLong() ? "Buy/Long" : "Sell/Short",
                        p.getQuantity(),
                        String.format("%,.2f", p.getAvgPrice()),
                        String.format("%,.2f", currentPrice),
                        String.format("%,.2f", unrealizedPnL),
                        String.format("%.2f%%", returnRate * 100)
                });
            }
        } catch (NumberFormatException e) {

        }
    }
}