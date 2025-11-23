package framework_and_driver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;

import entity.ChartViewModel;
import entity.TimeInterval;

/**
 * Reusable chart panel component that can be used in any Swing container.
 * Supports both line charts and candlestick charts.
 */
public class ChartPanel extends JPanel {

    private JPanel chartContainer;

    public ChartPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setBackground(Color.WHITE);
        
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        
        // Initialize with placeholder
        JLabel placeholder = new JLabel("<html><div style='text-align: center; padding: 20px;'>" +
                "<p>📊 Loading chart data...</p></div></html>", SwingConstants.CENTER);
        chartContainer.add(placeholder, BorderLayout.CENTER);
        add(chartContainer, BorderLayout.CENTER);
    }

    /**
     * Update chart data
     * @param viewModel Chart view model
     */
    public void updateChart(ChartViewModel viewModel) {
        // Remove existing chart
        chartContainer.removeAll();
        
        try {
            if (viewModel.isCandlestick()) {
                // Create candlestick chart (using CategoryChart with multiple series)
                CategoryChart chart = createCandlestickChart(viewModel);
                XChartPanel<CategoryChart> chartPanelComponent = new XChartPanel<>(chart);
                chartContainer.add(chartPanelComponent, BorderLayout.CENTER);
            } else {
                // Create line chart
                CategoryChart chart = createLineChart(viewModel);
                XChartPanel<CategoryChart> chartPanelComponent = new XChartPanel<>(chart);
                chartContainer.add(chartPanelComponent, BorderLayout.CENTER);
            }
            
            chartContainer.revalidate();
            chartContainer.repaint();
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("<html><div style='text-align: center; padding: 20px; color: red;'>" +
                    "<p>Chart Rendering Error: " + e.getMessage() + "</p></div></html>", 
                    SwingConstants.CENTER);
            chartContainer.add(errorLabel, BorderLayout.CENTER);
            chartContainer.revalidate();
            chartContainer.repaint();
        }
    }

    /**
     * Clear the chart
     */
    public void clearChart() {
        chartContainer.removeAll();
        JLabel placeholder = new JLabel("<html><div style='text-align: center; padding: 20px;'>" +
                "<p>📊 Loading chart data...</p></div></html>", SwingConstants.CENTER);
        chartContainer.add(placeholder, BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    /**
     * Display error message in the chart area
     */
    public void displayError(String message) {
        chartContainer.removeAll();
        JLabel errorLabel = new JLabel("<html><div style='text-align: center; padding: 20px; color: red;'>" +
                "<h3>⚠️ Error Loading Chart Data</h3>" +
                "<p>" + message + "</p>" +
                "<p style='font-size: 10px; color: gray;'>Possible reasons: API rate limit exceeded, network error, or invalid data.</p>" +
                "</div></html>", 
                SwingConstants.CENTER);
        chartContainer.add(errorLabel, BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    private CategoryChart createLineChart(ChartViewModel viewModel) {
        CategoryChart chart = new CategoryChartBuilder()
                .width(getWidth() > 0 ? getWidth() - 20 : 1200)
                .height(getHeight() > 0 ? getHeight() - 20 : 700)
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
        List<String> displayLabels = new ArrayList<>();
        List<Double> displayPrices = new ArrayList<>();
        
        // Determine max points based on interval
        // For larger charts, we can show more data points
        int maxPoints;
        TimeInterval interval = viewModel.getInterval();
        if (interval != null) {
            switch (interval) {
                case FIVE_MINUTES: // 5 minutes - show all 5-minute intervals (about 78 points per day)
                    maxPoints = 500; // Can show multiple days of 5-minute data
                    break;
                case DAILY: // Daily - show daily data points
                    maxPoints = 500; // Can show up to 500 days (about 1.5 years)
                    break;
                case WEEKLY: // Weekly - show weekly data points
                    maxPoints = 200; // Can show up to 200 weeks (about 4 years)
                    break;
                case MONTHLY: // Monthly - show monthly data points
                    maxPoints = 120; // Can show up to 120 months (10 years)
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
            for (int i = 0; i < dataSize; i += step) {
                displayLabels.add(labels.get(i));
                displayPrices.add(prices.get(i));
            }
            // Add the last point
            if (dataSize > 0 && (dataSize - 1) % step != 0) {
                displayLabels.add(labels.get(dataSize - 1));
                displayPrices.add(prices.get(dataSize - 1));
            }
        } else {
            // Use all points for smaller datasets
            displayLabels = labels;
            displayPrices = prices;
        }

        // Limit X-axis labels based on interval - show more labels for larger charts
        // Instead of using empty strings, we only keep the labels we want to show
        int labelCount = displayLabels.size();
        int maxLabels; // Maximum number of labels to show on X-axis
        if (interval != null) {
            switch (interval) {
                case FIVE_MINUTES: // 5 minutes - show more labels (every 5 minutes)
                    maxLabels = 80; // Can show up to 80 labels for intraday data
                    break;
                case DAILY: // Daily - show daily labels
                    maxLabels = 90; // Can show up to 90 days (about 3 months)
                    break;
                case WEEKLY: // Weekly - show weekly labels
                    maxLabels = 52; // Can show up to 52 weeks (1 year)
                    break;
                case MONTHLY: // Monthly - show monthly labels
                    maxLabels = 24; // Can show up to 24 months (2 years)
                    break;
                default:
                    maxLabels = 50;
            }
        } else {
            maxLabels = 50; // Default
        }
        
        if (labelCount > maxLabels) {
            List<String> axisLabels = new ArrayList<>();
            List<Double> axisPrices = new ArrayList<>();
            int labelStep = Math.max(1, labelCount / maxLabels);
            for (int i = 0; i < labelCount; i++) {
                if (i % labelStep == 0 || i == labelCount - 1) {
                    axisLabels.add(displayLabels.get(i));
                    axisPrices.add(displayPrices.get(i));
                }
            }
            displayLabels = axisLabels;
            displayPrices = axisPrices;
        }

        chart.addSeries("Price", displayLabels, displayPrices);
        
        return chart;
    }

    private CategoryChart createCandlestickChart(ChartViewModel viewModel) {
        CategoryChart chart = new CategoryChartBuilder()
                .width(getWidth() > 0 ? getWidth() - 20 : 1200)
                .height(getHeight() > 0 ? getHeight() - 20 : 700)
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
        // For larger charts, we can show more data points
        int maxPoints;
        TimeInterval interval = viewModel.getInterval();
        if (interval != null) {
            switch (interval) {
                case FIVE_MINUTES: // 5 minutes - show all 5-minute intervals (about 78 points per day)
                    maxPoints = 500; // Can show multiple days of 5-minute data
                    break;
                case DAILY: // Daily - show daily data points
                    maxPoints = 500; // Can show up to 500 days (about 1.5 years)
                    break;
                case WEEKLY: // Weekly - show weekly data points
                    maxPoints = 200; // Can show up to 200 weeks (about 4 years)
                    break;
                case MONTHLY: // Monthly - show monthly data points
                    maxPoints = 120; // Can show up to 120 months (10 years)
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

        // Limit X-axis labels based on interval - show more labels for larger charts
        // Instead of using empty strings, we only keep the labels we want to show
        int labelCount = displayLabels.size();
        int maxLabels; // Maximum number of labels to show on X-axis
        if (interval != null) {
            switch (interval) {
                case FIVE_MINUTES: // 5 minutes - show more labels (every 5 minutes)
                    maxLabels = 80; // Can show up to 80 labels for intraday data
                    break;
                case DAILY: // Daily - show daily labels
                    maxLabels = 90; // Can show up to 90 days (about 3 months)
                    break;
                case WEEKLY: // Weekly - show weekly labels
                    maxLabels = 52; // Can show up to 52 weeks (1 year)
                    break;
                case MONTHLY: // Monthly - show monthly labels
                    maxLabels = 24; // Can show up to 24 months (2 years)
                    break;
                default:
                    maxLabels = 50;
            }
        } else {
            maxLabels = 50; // Default
        }
        
        if (labelCount > maxLabels) {
            List<String> axisLabels = new ArrayList<>();
            List<Double> axisPrices = new ArrayList<>();
            int labelStep = Math.max(1, labelCount / maxLabels);
            for (int i = 0; i < labelCount; i++) {
                if (i % labelStep == 0 || i == labelCount - 1) {
                    axisLabels.add(displayLabels.get(i));
                    axisPrices.add(displayPrices.get(i));
                }
            }
            displayLabels = axisLabels;
            displayPrices = axisPrices;
        }

        // Show only close price as a line chart (cleaner than showing all OHLC)
        chart.addSeries("Price", displayLabels, displayPrices);
        
        return chart;
    }

}

