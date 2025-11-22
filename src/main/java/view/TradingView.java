package view;

import interface_adapter.simulated_trading.TradingController;
import interface_adapter.simulated_trading.TradingState;
import interface_adapter.simulated_trading.TradingViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TradingView extends JPanel implements ActionListener, PropertyChangeListener {

    public final String viewName = "trading";
    private final TradingViewModel viewModel;
    private final TradingController controller;

    // UI Components
    private final JLabel availableCashLabel = new JLabel("Loading...");
    private final JLabel totalProfitLabel = new JLabel("Loading...");
    private final JLabel totalReturnLabel = new JLabel("0.00%");
    private final JLabel maxDrawdownLabel = new JLabel("0.00");

    private final JTextField amountField = new JTextField(10);
    private final JButton buyButton = new JButton(TradingViewModel.BUY_BUTTON_LABEL);
    private final JButton sellButton = new JButton(TradingViewModel.SELL_BUTTON_LABEL);

    // Custom Chart
    private final PriceChartPanel chartPanel = new PriceChartPanel();

    // Timer for Simulation Loop (1000ms = 1 second)
    private final Timer timer = new Timer(1000, this);

    public TradingView(TradingController controller, TradingViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        this.setLayout(new BorderLayout());

        // --- Top Panel (Cash) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel cashTitle = new JLabel("Available Virtual Money: ");
        cashTitle.setFont(new Font("Arial", Font.BOLD, 16));
        availableCashLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(cashTitle);
        topPanel.add(availableCashLabel);
        this.add(topPanel, BorderLayout.NORTH);

        // --- Center Panel (Chart) ---
        this.add(chartPanel, BorderLayout.CENTER);

        // --- Right Panel (Controls) ---
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        rightPanel.add(new JLabel(TradingViewModel.AMOUNT_LABEL));
        rightPanel.add(amountField);
        rightPanel.add(Box.createVerticalStrut(10));

        buyButton.setBackground(Color.GREEN);
        buyButton.setOpaque(true);
        rightPanel.add(buyButton);

        rightPanel.add(Box.createVerticalStrut(10));

        sellButton.setBackground(Color.RED);
        sellButton.setOpaque(true);
        rightPanel.add(sellButton);

        // Bind Buttons
        buyButton.addActionListener(e -> handleTrade(true));
        sellButton.addActionListener(e -> handleTrade(false));

        this.add(rightPanel, BorderLayout.EAST);

        // --- Bottom Panel (Stats) ---
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Portfolio Summary"));

        bottomPanel.add(createStatPanel("Total Equity", totalProfitLabel));
        bottomPanel.add(createStatPanel("Return Rate", totalReturnLabel));
        bottomPanel.add(createStatPanel("Max Drawdown", maxDrawdownLabel));

        this.add(bottomPanel, BorderLayout.SOUTH);

        // Start the heartbeat
        timer.start();
    }

    private JPanel createStatPanel(String title, JLabel valueLabel) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(title), BorderLayout.NORTH);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }

    private void handleTrade(boolean isBuy) {
        try {
            double amount = Double.parseDouble(amountField.getText());

            // Clean up price string to double (remove $ and ,)
            String priceStr = viewModel.getState().getCurrentPrice()
                    .replace("$", "").replace(",", "");
            double currentPrice = Double.parseDouble(priceStr);

            controller.executeTrade("AAPL", amount, isBuy, currentPrice);
            amountField.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount and wait for price.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        controller.executeTimerTick();
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

        chartPanel.updateData(state.getChartData());
    }
}