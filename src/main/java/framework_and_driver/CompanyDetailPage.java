package framework_and_driver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import entity.CompanyDetailViewModel;
import interface_adapter.controller.IntervalController;

/**
 * Company detail page UI component that displays stock chart.
 */
public class CompanyDetailPage extends JFrame {

    private ChartPanel chartPanel;
    private IntervalController chartController;
    private String currentTicker;

    public CompanyDetailPage() {
        super("Company Details");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 1000);
        setLayout(new BorderLayout());

        // Add chart panel
        add(buildChartPanel(), BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    // ---------------------------------------------------------
    // LEFT SIDE — CHART PANEL (Revised)
    // ---------------------------------------------------------
    private JPanel buildChartPanel() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Company Chart", SwingConstants.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JButton tradeButton = new JButton("Trade");
        tradeButton.setBackground(new Color(200, 200, 200));
        tradeButton.setFocusPainted(false);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(tradeButton, BorderLayout.EAST);

        // Chart panel
        chartPanel = new ChartPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(1200, 700));
        chartPanel.setMinimumSize(new java.awt.Dimension(1000, 600));

        // Interval buttons
        JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton btn5min = new JButton("5min");
        JButton btn1day = new JButton("1 day");
        JButton btn1week = new JButton("1 week");
        JButton zoomIn = new JButton("Zoom in");

        btn5min.addActionListener(e -> {
            if (chartController != null && currentTicker != null) {
                chartController.handleTimeChange("5M");
            }
        });
        btn1day.addActionListener(e -> {
            if (chartController != null && currentTicker != null) {
                chartController.handleTimeChange("1D");
            }
        });
        btn1week.addActionListener(e -> {
            if (chartController != null && currentTicker != null) {
                chartController.handleTimeChange("1W");
            }
        });

        // Bind Zoom In button event (this was missing!)
        zoomIn.addActionListener(e -> {
            if (chartPanel != null) {
                chartPanel.performZoom(); // Call the public method we wrote in ChartPanel
            }
        });

        intervalPanel.add(btn5min);
        intervalPanel.add(btn1day);
        intervalPanel.add(btn1week);
        intervalPanel.add(zoomIn);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(intervalPanel, BorderLayout.SOUTH);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Update company details UI with view model data
     */
    public void updateCompanyDetails(CompanyDetailViewModel viewModel) {
        if (viewModel == null) {
            return;
        }

        // Store ticker for chart updates
        this.currentTicker = viewModel.getTicker();
        
        // Update chart controller with new ticker
        if (chartController != null) {
            chartController.setCurrentTicker(this.currentTicker);
        }
        
        // Tell ChartPanel the current stock ticker, otherwise it won't know which stock to zoom
        if (chartPanel != null) {
            chartPanel.enableZoom(this.currentTicker);
        }
    }

    /**
     * Update the chart with new data
     */
    public void updateChart(entity.ChartViewModel chartViewModel) {
        if (chartPanel != null) {
            chartPanel.updateChart(chartViewModel);
        }
    }

    /**
     * Set the chart controller for time interval changes
     */
    public void setChartController(IntervalController controller) {
        this.chartController = controller;
    }

    /**
     * Display error message
     */
    public void displayError(String message) {
        javax.swing.JOptionPane.showMessageDialog(this, message, "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Get the chart panel for error display
     */
    public ChartPanel getChartPanel() {
        return chartPanel;
    }
}
