package frameworkanddriver;

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
import interfaceadapter.controller.IntervalController;

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
        final JPanel headerPanel = new JPanel(new BorderLayout());
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
        final JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton btn5min = new JButton("5min");
        JButton btn1day = new JButton("1 day");
        JButton btn1week = new JButton("1 week");
        final JButton zoomIn = new JButton("Zoom in");

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
                chartPanel.performZoom();
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
     * Updates the company details section of the UI using the data provided
     * by the given {@link CompanyDetailViewModel}. If the supplied view model
     * is {@code null}, no update is performed.
     *
     * @param viewModel the view model containing the latest company detail data
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
     * Updates the chart display using the provided {@link entity.ChartViewModel}.
     * If the chart panel has not been initialized, the update request is ignored.
     *
     * @param chartViewModel the view model containing the latest chart data
     */
    public void updateChart(entity.ChartViewModel chartViewModel) {
        if (chartPanel != null) {
            chartPanel.updateChart(chartViewModel);
        }
    }

    /**
     * Sets the chart controller responsible for handling time interval
     * selection and related chart update actions.
     *
     * @param controller the interval controller to associate with this chart view
     */
    public void setChartController(IntervalController controller) {
        this.chartController = controller;
    }

    /**
     * Displays an error message to the user in a modal dialog. This method uses
     * a standard Swing {@link javax.swing.JOptionPane} to show the message with
     * an error icon.
     *
     * @param message the error message to display
     */
    public void displayError(String message) {
        javax.swing.JOptionPane.showMessageDialog(this, message, "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Returns the underlying {@link ChartPanel} used by this view component.
     * This allows presenters or controllers to send error messages or chart updates
     * directly to the panel when needed.
     *
     * @return the chart panel associated with this view
     */
    public ChartPanel getChartPanel() {
        return chartPanel;
    }
}
