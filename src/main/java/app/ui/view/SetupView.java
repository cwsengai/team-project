package app.ui.view;

import interfaceadapter.setup_simulation.SetupController;
import interfaceadapter.setup_simulation.SetupViewModel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class SetupView extends JPanel implements PropertyChangeListener {

    @SuppressWarnings("checkstyle:VisibilityModifier")
    public final String viewName = SetupViewModel.VIEW_NAME;
    private final SetupController controller;
    private final SetupViewModel viewModel;

    private final JTextField tickerField = new JTextField(10);
    private final JTextField balanceField = new JTextField("100000.00", 10);
    private final JComboBox<String> speedComboBox;
    private final JButton startButton = new JButton(SetupViewModel.START_BUTTON_LABEL);
    private final JLabel errorLabel = new JLabel(" ");

    @SuppressWarnings({"checkstyle:MemberName", "checkstyle:AbbreviationAsWordInName"})
    private final Color BG_COLOR = new Color(245, 247, 250);
    @SuppressWarnings({"checkstyle:MemberName", "checkstyle:AbbreviationAsWordInName"})
    private final Color CARD_COLOR = Color.WHITE;
    @SuppressWarnings({"checkstyle:MemberName", "checkstyle:AbbreviationAsWordInName"})
    private final Color PRIMARY_COLOR = new Color(46, 204, 113);
    @SuppressWarnings({"checkstyle:MemberName", "checkstyle:AbbreviationAsWordInName"})
    private final Color TEXT_COLOR = new Color(52, 73, 94);

    @SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:MemberName", "checkstyle:AbbreviationAsWordInName"})
    private final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 18);
    @SuppressWarnings({"checkstyle:MemberName", "checkstyle:AbbreviationAsWordInName"})
    private final Font INPUT_FONT = new Font("SansSerif", Font.PLAIN, 16);
    @SuppressWarnings({"checkstyle:MemberName", "checkstyle:AbbreviationAsWordInName"})
    private final Font HINT_FONT = new Font("SansSerif", Font.ITALIC, 14);

    @SuppressWarnings({"checkstyle:LambdaParameterName", "checkstyle:MagicNumber", "checkstyle:FinalLocalVariable", "checkstyle:LineLength", "checkstyle:EmptyLineSeparator", "checkstyle:RegexpMultiline", "checkstyle:RegexpSinglelineJava", "checkstyle:JavaNCSS", "checkstyle:ExecutableStatementCount"})
    public SetupView(SetupController controller, SetupViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

        this.setLayout(new BorderLayout());
        this.setBackground(BG_COLOR);

        // --- TOP HEADER (Logo + Error) ---
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.setBorder(new EmptyBorder(60, 0, 30, 0));

        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        JLabel logoLabel = new JLabel("✶ BILLIONAIRE");
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        logoLabel.setForeground(new Color(44, 62, 80));
        logoPanel.add(logoLabel);

        // Error Label
        JPanel errorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        errorPanel.setOpaque(false);
        errorLabel.setForeground(new Color(231, 76, 60));
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        errorPanel.add(errorLabel);

        topContainer.add(logoPanel);
        topContainer.add(errorPanel);
        this.add(topContainer, BorderLayout.NORTH);

        // --- CENTER FORM (The "Card") ---
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 30, 35));
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 2, true),
                new EmptyBorder(60, 60, 60, 60)
        ));

        // Input Fields Styling
        styleLabelAndInput(formPanel, "Ticker Symbol (e.g., AAPL):", tickerField);
        styleLabelAndInput(formPanel, "Initial Balance ($):", balanceField);

        // Speed ComboBox
        String[] speeds = {"5x", "10x", "20x", "30x"};
        speedComboBox = new JComboBox<>(speeds);
        speedComboBox.setSelectedItem("10x");
        speedComboBox.setFont(INPUT_FONT);
        speedComboBox.setBackground(Color.WHITE);

        JLabel speedLabel = new JLabel("Simulation Speed:");
        speedLabel.setFont(LABEL_FONT);
        speedLabel.setForeground(TEXT_COLOR);

        formPanel.add(speedLabel);
        formPanel.add(speedComboBox);


        JLabel hintSpacer = new JLabel("");
        JLabel speedHint = new JLabel("<html><body style='width: 200px'>Note: 10x speed means 1 minute in real life equals 10 minutes in simulation.</body></html>");
        speedHint.setFont(HINT_FONT);
        speedHint.setForeground(Color.GRAY);

        formPanel.add(hintSpacer);
        formPanel.add(speedHint);

        centerWrapper.add(formPanel);
        this.add(centerWrapper, BorderLayout.CENTER);

        // --- BOTTOM BUTTON ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(30, 0, 80, 0));

        startButton.setFont(new Font("SansSerif", Font.BOLD, 22));
        startButton.setPreferredSize(new Dimension(250, 65));
        startButton.setBackground(PRIMARY_COLOR);
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setOpaque(true);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomPanel.add(startButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        // Bind Action
        startButton.addActionListener(e -> handleStart());
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:FinalLocalVariable"})
    private void styleLabelAndInput(JPanel panel, String labelText, JTextField textField) {
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);

        textField.setFont(INPUT_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 2),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        panel.add(label);
        panel.add(textField);
    }

    @SuppressWarnings({"checkstyle:RegexpSinglelineJava", "checkstyle:RightCurly", "checkstyle:FinalLocalVariable"})
    private void handleStart() {
        errorLabel.setText(" ");
        try {
            String ticker = tickerField.getText().toUpperCase();
            double balance = Double.parseDouble(balanceField.getText());
            int speed = Integer.parseInt(speedComboBox.getSelectedItem().toString().replace("x", ""));

            controller.execute(ticker, balance, speed);

        } catch (NumberFormatException ex) {
            errorLabel.setText("⚠️ Input Error: Balance/Speed must be valid numbers.");
        }
    }

    @SuppressWarnings("checkstyle:RegexpSinglelineJava")
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("error".equals(evt.getPropertyName())) {
            errorLabel.setText("⚠️ " + viewModel.getError());
        }
    }

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public void setInitialSymbol(String symbol) {
        tickerField.setText(symbol);
    }
}