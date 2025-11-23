package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.format.DateTimeParseException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import interface_adapter.setup_simulation.SetupController;
import interface_adapter.setup_simulation.SetupViewModel;

public class SetupView extends JPanel implements PropertyChangeListener {

    public final String viewName = SetupViewModel.VIEW_NAME;
    private final SetupController controller;
    private final SetupViewModel viewModel;

    // UI Components
    private final JTextField tickerField = new JTextField(10);
    private final JTextField balanceField = new JTextField("100000.00", 10);
    private final JTextField startDateField = new JTextField("2024-01-01", 10);
    private final JTextField endDateField = new JTextField("2024-01-31", 10);

    // Speed Options (5x, 10x, 20x, 30x)
    private final JComboBox<String> speedComboBox;

    private final JButton startButton = new JButton(SetupViewModel.START_BUTTON_LABEL);
    private final JLabel errorLabel = new JLabel(" "); // Space to prevent layout jump

    public SetupView(SetupController controller, SetupViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        this.setLayout(new BorderLayout(20, 20));
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(50, 50, 50, 50),
                BorderFactory.createTitledBorder(SetupViewModel.TITLE_LABEL)
        ));

        // --- 1. Main Input Panel ---
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        // Ticker (Stock Symbol)
        inputPanel.add(new JLabel("Ticker (e.g., AAPL):"));
        inputPanel.add(tickerField);

        // Initial Balance
        inputPanel.add(new JLabel("Initial Balance ($):"));
        inputPanel.add(balanceField);

        // Start Date
        inputPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        inputPanel.add(startDateField);

        // End Date
        inputPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        inputPanel.add(endDateField);

        // Speed Multiplier
        String[] speeds = {"5x", "10x", "20x", "30x"};
        speedComboBox = new JComboBox<>(speeds);
        speedComboBox.setSelectedItem("10x");
        inputPanel.add(new JLabel("Speed Multiplier:"));
        inputPanel.add(speedComboBox);

        // --- 2. Button and Error ---
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout());
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        controlPanel.add(startButton);

        this.add(errorLabel, BorderLayout.NORTH); // Display errors here
        this.add(inputPanel, BorderLayout.CENTER);
        this.add(controlPanel, BorderLayout.SOUTH);

        // Bind Action to Controller
        startButton.addActionListener(e -> handleStart());
    }

    private void handleStart() {
        errorLabel.setText(" ");
        try {
            String ticker = tickerField.getText().toUpperCase();
            double balance = Double.parseDouble(balanceField.getText());
            int speed = Integer.parseInt(speedComboBox.getSelectedItem().toString().replace("x", ""));

            String startDateStr = startDateField.getText();
            String endDateStr = endDateField.getText();

            controller.execute(ticker, balance, speed, startDateStr, endDateStr);
        } catch (NumberFormatException ex) {
            errorLabel.setText("ERROR: Balance/Speed must be a number.");
        } catch (DateTimeParseException ex) {
            errorLabel.setText("ERROR: Date format must be YYYY-MM-DD.");
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("error".equals(evt.getPropertyName())) {
            errorLabel.setText("ERROR: " + viewModel.getError());
        }
    }
}