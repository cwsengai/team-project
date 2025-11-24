package view;

import interface_adapter.setup_simulation.SetupController;
import interface_adapter.setup_simulation.SetupViewModel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SetupView extends JPanel implements PropertyChangeListener {

    public final String viewName = SetupViewModel.VIEW_NAME;
    private final SetupController controller;
    private final SetupViewModel viewModel;

    // UI Components
    private final JTextField tickerField = new JTextField(10);
    private final JTextField balanceField = new JTextField("100000.00", 10);


    // Speed Options (5x, 10x, 20x, 30x)
    private final JComboBox<String> speedComboBox;

    private final JButton startButton = new JButton(SetupViewModel.START_BUTTON_LABEL);
    private final JLabel errorLabel = new JLabel(" ");

    public SetupView(SetupController controller, SetupViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        this.setLayout(new BorderLayout(20, 20));

        Border outerPadding = BorderFactory.createEmptyBorder(30, 50, 50, 50);
        this.setBorder(outerPadding);

        // --- 1. TOP HEADER (Logo + Error) ---
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setOpaque(false);
        JLabel logoLabel = new JLabel("💰 BILLIONAIRE");
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        logoLabel.setForeground(new Color(50, 50, 50));
        logoPanel.add(logoLabel);

        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel errorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        errorPanel.setOpaque(false);
        errorPanel.add(errorLabel);

        topContainer.add(logoPanel);
        topContainer.add(Box.createVerticalStrut(10));
        topContainer.add(errorPanel);

        this.add(topContainer, BorderLayout.NORTH);

        // --- 2. Main Input Panel (Form) ---

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 20));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Simulation Configuration"));

        // Ticker (Stock Symbol)
        inputPanel.add(new JLabel("Ticker (e.g., AAPL):"));
        inputPanel.add(tickerField);

        // Initial Balance
        inputPanel.add(new JLabel("Initial Balance ($):"));
        inputPanel.add(balanceField);


        // Speed Multiplier
        String[] speeds = {"5x", "10x", "20x", "30x"};
        speedComboBox = new JComboBox<>(speeds);
        speedComboBox.setSelectedItem("10x");
        inputPanel.add(new JLabel("Speed Multiplier:"));
        inputPanel.add(speedComboBox);

        this.add(inputPanel, BorderLayout.CENTER);

        // --- 3. Bottom Button ---
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setPreferredSize(new Dimension(120, 40));
        startButton.setBackground(new Color(60, 179, 113));
        startButton.setForeground(Color.BLACK);
        startButton.setOpaque(true);
        startButton.setBorderPainted(false);

        controlPanel.add(startButton);

        this.add(controlPanel, BorderLayout.SOUTH);

        // Bind Action
        startButton.addActionListener(e -> handleStart());
    }

    private void handleStart() {
        errorLabel.setText(" "); // Clear old error
        try {
            String ticker = tickerField.getText().toUpperCase();
            double balance = Double.parseDouble(balanceField.getText());
            int speed = Integer.parseInt(speedComboBox.getSelectedItem().toString().replace("x", ""));

            controller.execute(ticker, balance, speed);

        } catch (NumberFormatException ex) {
            errorLabel.setText("ERROR: Balance/Speed must be a number.");
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("error".equals(evt.getPropertyName())) {
            errorLabel.setText("ERROR: " + viewModel.getError());
        }
    }
}