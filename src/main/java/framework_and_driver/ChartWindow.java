package framework_and_driver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;

import entity.ChartViewModel;
import entity.TimeInterval;
import interface_adapter.IntervalController;

public class ChartWindow extends JFrame {

    private JPanel chartPanel; // Panel to hold the XChart
    private final JTextArea statusArea = new JTextArea(3, 40); // Used to display Presenter's output
    private IntervalController controller;

    public ChartWindow() {
        super("Stock Chart Platform - UC4 Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // --- 1. top controller ---
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // --- 2. chart part ---
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chartPanel.setBackground(Color.WHITE);
        
        // Initialize with placeholder
        JLabel placeholder = new JLabel("<html><div style='text-align: center; padding: 50px;'>" +
                "<h2>Dow Jones Industrial Average</h2>" +
                "<p>📊 Loading chart data...</p></div></html>", SwingConstants.CENTER);
        placeholder.setFont(new Font("SansSerif", Font.PLAIN, 14));
        chartPanel.add(placeholder, BorderLayout.CENTER);
        add(chartPanel, BorderLayout.CENTER);

        // --- 3. bottom control and stuation ---
        JPanel southPanel = createSouthPanel();
        add(southPanel, BorderLayout.SOUTH);

        pack();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        // LOGO
        JLabel logo = new JLabel("✶ BILLIONAIRE", SwingConstants.LEFT);
        logo.setFont(new Font("SansSerif", Font.BOLD, 16));

        // login
        JButton loginButton = new JButton("Signup/ Login");
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(Color.BLACK);
        loginButton.setFocusPainted(false);

        panel.add(logo, BorderLayout.WEST);
        panel.add(loginButton, BorderLayout.EAST);
        return panel;
    }

    private JPanel createSouthPanel() {
        JPanel southPanel = new JPanel(new BorderLayout());

        // Button group (5M, 1D, 1W)
        JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        createIntervalButton(intervalPanel, "5M");
        createIntervalButton(intervalPanel, "1D");
        createIntervalButton(intervalPanel, "1W");

        // Status information and Back button
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        controlPanel.add(intervalPanel, BorderLayout.WEST);
        controlPanel.add(statusArea, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(Color.BLACK);
        controlPanel.add(backButton, BorderLayout.EAST);

        southPanel.add(controlPanel, BorderLayout.CENTER);

        return southPanel;
    }

    private void createIntervalButton(JPanel panel, String text) {
        JButton button = new JButton(text);
        button.addActionListener((ActionEvent e) -> {
            // Delegate button click event to Controller
            if (controller != null) {
                statusArea.append(">>> Requesting: " + text + "\n");
                controller.handleTimeChange(text);
            }
        });
        panel.add(button);
    }

    // --- Controller Setter ---
    public void setController(IntervalController controller) {
        this.controller = controller;
    }

    // --- Presenter Output ---
    public void updateChart(ChartViewModel viewModel) {
        // Remove existing chart
        chartPanel.removeAll();
        
        try {
            if (viewModel.isCandlestick()) {
                // Create candlestick chart (using CategoryChart with multiple series)
                CategoryChart chart = createCandlestickChart(viewModel);
                XChartPanel<CategoryChart> chartPanelComponent = new XChartPanel<>(chart);
                chartPanel.add(chartPanelComponent, BorderLayout.CENTER);
                statusArea.append("✅ Success: Candlestick Chart - " + viewModel.getTitle() + 
                    " (Data Points = " + viewModel.getLabels().size() + ")\n");
            } else {
                // Create line chart
                CategoryChart chart = createLineChart(viewModel);
                XChartPanel<CategoryChart> chartPanelComponent = new XChartPanel<>(chart);
                chartPanel.add(chartPanelComponent, BorderLayout.CENTER);
                statusArea.append("✅ Success: Line Chart - " + viewModel.getTitle() + 
                    " (Data Points = " + viewModel.getPrices().size() + ")\n");
            }
            
            chartPanel.revalidate();
            chartPanel.repaint();
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("<html><div style='text-align: center; padding: 50px; color: red;'>" +
                    "<h3>Chart Rendering Error</h3><p>" + e.getMessage() + "</p></div></html>", 
                    SwingConstants.CENTER);
            chartPanel.add(errorLabel, BorderLayout.CENTER);
            chartPanel.revalidate();
            chartPanel.repaint();
            statusArea.append("❌ ERROR: " + e.getMessage() + "\n");
        }
    }

    private CategoryChart createLineChart(ChartViewModel viewModel) {
        CategoryChart chart = new CategoryChartBuilder()
                .width(800)
                .height(500)
                .title(viewModel.getTitle())
                .xAxisTitle("Date")
                .yAxisTitle("Price (USD)")
                .build();

        // Customize chart with line chart specific styling
        ChartStyler.applyLineChartStyle(chart);

        // Convert labels and prices to arrays
        List<String> labels = viewModel.getLabels();
        List<Double> prices = viewModel.getPrices();
        
        // Sample data points based on time interval for better visualization
        int dataSize = labels.size();
        List<String> displayLabels;
        List<Double> displayPrices;
        
        // Determine max points based on interval
        int maxPoints;
        TimeInterval interval = viewModel.getInterval();
        if (interval != null) {
            switch (interval) {
                case INTRADAY: // 5 minutes - show more detail
                    maxPoints = 500;
                    break;
                case DAILY: // Daily - show moderate detail
                    maxPoints = 100;
                    break;
                case WEEKLY: // Weekly - show less detail
                    maxPoints = 50;
                    break;
                case MONTHLY: // Monthly - show minimal detail
                    maxPoints = 30;
                    break;
                default:
                    maxPoints = 200;
            }
        } else {
            maxPoints = 200; // Default
        }
        
        // Sample data points if needed
        if (dataSize > maxPoints) {
            int step = Math.max(1, dataSize / maxPoints);
            displayLabels = new ArrayList<>();
            displayPrices = new ArrayList<>();
            for (int i = 0; i < dataSize; i += step) {
                displayLabels.add(labels.get(i));
                displayPrices.add(prices.get(i));
            }
            if (dataSize > 0 && (dataSize - 1) % step != 0) {
                displayLabels.add(labels.get(dataSize - 1));
                displayPrices.add(prices.get(dataSize - 1));
            }
        } else {
            displayLabels = labels;
            displayPrices = prices;
        }

        // Limit X-axis labels to avoid overcrowding (max 15-20 labels)
        int labelCount = displayLabels.size();
        int maxLabels = 15; // Maximum number of labels to show on X-axis
        if (labelCount > maxLabels) {
            List<String> axisLabels = new ArrayList<>();
            int labelStep = Math.max(1, labelCount / maxLabels);
            for (int i = 0; i < labelCount; i++) {
                if (i % labelStep == 0 || i == labelCount - 1) {
                    axisLabels.add(displayLabels.get(i));
                } else {
                    axisLabels.add(""); // Empty string to hide label
                }
            }
            displayLabels = axisLabels;
        }

        chart.addSeries("Price", displayLabels, displayPrices);
        
        return chart;
    }

    private CategoryChart createCandlestickChart(ChartViewModel viewModel) {
        CategoryChart chart = new CategoryChartBuilder()
                .width(800)
                .height(500)
                .title(viewModel.getTitle())
                .xAxisTitle("Date")
                .yAxisTitle("Price (USD)")
                .build();

        // For candlestick data, show only close price as a line chart for cleaner visualization
        // This matches the UI design better
        ChartStyler.applyLineChartStyle(chart);

        List<String> labels = viewModel.getLabels();
        List<Double> closePrices = viewModel.getClosePrices();
        
        // Sample data points based on time interval
        int dataSize = labels.size();
        List<String> displayLabels;
        List<Double> displayPrices;
        
        // Determine max points based on interval
        int maxPoints;
        TimeInterval interval = viewModel.getInterval();
        if (interval != null) {
            switch (interval) {
                case INTRADAY: // 5 minutes - show more detail
                    maxPoints = 500;
                    break;
                case DAILY: // Daily - show moderate detail
                    maxPoints = 100;
                    break;
                case WEEKLY: // Weekly - show less detail
                    maxPoints = 50;
                    break;
                case MONTHLY: // Monthly - show minimal detail
                    maxPoints = 30;
                    break;
                default:
                    maxPoints = 200;
            }
        } else {
            maxPoints = 200; // Default
        }
        
        // Sample data points if needed
        if (dataSize > maxPoints) {
            int step = Math.max(1, dataSize / maxPoints);
            displayLabels = new ArrayList<>();
            displayPrices = new ArrayList<>();
            for (int i = 0; i < dataSize; i += step) {
                displayLabels.add(labels.get(i));
                displayPrices.add(closePrices.get(i));
            }
            if (dataSize > 0 && (dataSize - 1) % step != 0) {
                displayLabels.add(labels.get(dataSize - 1));
                displayPrices.add(closePrices.get(dataSize - 1));
            }
        } else {
            displayLabels = labels;
            displayPrices = closePrices;
        }

        // Limit X-axis labels to avoid overcrowding (max 15-20 labels)
        int labelCount = displayLabels.size();
        int maxLabels = 15; // Maximum number of labels to show on X-axis
        if (labelCount > maxLabels) {
            List<String> axisLabels = new ArrayList<>();
            int labelStep = Math.max(1, labelCount / maxLabels);
            for (int i = 0; i < labelCount; i++) {
                if (i % labelStep == 0 || i == labelCount - 1) {
                    axisLabels.add(displayLabels.get(i));
                } else {
                    axisLabels.add(""); // Empty string to hide label
                }
            }
            displayLabels = axisLabels;
        }

        // Show only close price as a line chart (cleaner than showing all OHLC)
        chart.addSeries("Price", displayLabels, displayPrices);
        
        return chart;
    }

    public void displayError(String message) {
        JOptionPane.showMessageDialog(this, message, "UC4 Error", JOptionPane.ERROR_MESSAGE);
        statusArea.append("❌ ERROR: " + message + "\n");
    }
}