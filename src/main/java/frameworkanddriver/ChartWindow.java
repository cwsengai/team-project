package frameworkanddriver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import entity.ChartViewModel;
import interfaceadapter.controller.IntervalController;

public class ChartWindow extends JFrame {

    private final ChartPanel chartPanel;
    private IntervalController controller;
    
    // UI Constants
    private final Color ACCENT_BLACK = new Color(20, 20, 20);
    private final Color BG_GREY = new Color(245, 245, 245);

    public ChartWindow() {
        super("Billionaire - Market Data");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.WHITE);
        
        // Main Container with White Background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        // --- 1. Top Header (Logo & Login) ---
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- 2. Chart Area ---
        // Use our custom ChartPanel
        chartPanel = new ChartPanel();
        mainPanel.add(chartPanel, BorderLayout.CENTER);

        // --- 3. Bottom Controls (Time Intervals & Back) ---
        JPanel southPanel = createSouthPanel();
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        // Landscape aspect ratio like screenshot
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // LEFT: Logo
        JLabel logo = new JLabel("❋ BILLIONAIRE");
        logo.setFont(new Font("SansSerif", Font.BOLD, 20));
        logo.setForeground(ACCENT_BLACK);

        // RIGHT: Signup/Login Button (Black pill shape style)
        JButton loginButton = new JButton("Signup/ Login");
        styleButton(loginButton, ACCENT_BLACK, Color.WHITE);
        loginButton.setPreferredSize(new Dimension(140, 35));

        panel.add(logo, BorderLayout.WEST);
        panel.add(loginButton, BorderLayout.EAST);
        return panel;
    }

    private JPanel createSouthPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // LEFT: Time Interval Buttons (5min, 1 day, 1 week)
        JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        intervalPanel.setBackground(Color.WHITE);
        
        createIntervalButton(intervalPanel, "5min", "5M");
        createIntervalButton(intervalPanel, "1 day", "1D");
        createIntervalButton(intervalPanel, "1 week", "1W");

        // RIGHT: Back / Zoom Buttons
        JPanel rightControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightControlPanel.setBackground(Color.WHITE);
        
        JButton backButton = new JButton("Back");
        // Black button
        styleButton(backButton, ACCENT_BLACK, Color.WHITE);
        rightControlPanel.add(backButton);

        panel.add(intervalPanel, BorderLayout.WEST);
        panel.add(rightControlPanel, BorderLayout.EAST);
        
        return panel;
    }

    private void createIntervalButton(JPanel panel, String label, String command) {
        JButton button = new JButton(label);
        // Style: Light Grey background, Black text (like screenshot unselected state)
        styleButton(button, BG_GREY, Color.BLACK);
        
        button.addActionListener((ActionEvent e) -> {
            if (controller != null) {
                // Visual feedback could be added here (change color to black when selected)
                controller.handleTimeChange(command);
            }
        });
        panel.add(button);
    }
    
    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        // Note: For true rounded corners in Swing, you'd need a custom ButtonUI, 
        // but this is clean enough for MVP.
        btn.setOpaque(true);
        btn.setBorderPainted(false); 
    }

    // --- Integration Methods ---

    public void setController(IntervalController controller) {
        this.controller = controller;
    }

    /**
     * Updates the chart display using the data provided by the given
     * {@link ChartViewModel}. This delegates the update to the underlying
     * chart panel component.
     *
     * @param viewModel the view model containing the latest chart data
     */
    public void updateChart(ChartViewModel viewModel) {
        // Pass data to the ChartPanel
        chartPanel.updateChart(viewModel);
    }

    /**
     * Displays an error message on the chart panel, typically used when
     * data retrieval or processing fails.
     *
     * @param message the error message to show to the user
     */
    public void displayError(String message) {
        chartPanel.displayError(message);
    }
}
