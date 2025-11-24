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

public class ChartPanel extends JPanel {

    private JPanel chartContainer;

    public ChartPanel() {
        setLayout(new BorderLayout());
        // Remove padding to let chart fill the space like in the screenshot
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 
        setBackground(Color.WHITE);
        
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        
        // Initial placeholder
        JLabel placeholder = new JLabel("Waiting for data...", SwingConstants.CENTER);
        chartContainer.add(placeholder, BorderLayout.CENTER);
        add(chartContainer, BorderLayout.CENTER);
    }

    public void updateChart(ChartViewModel viewModel) {
        chartContainer.removeAll();
        try {
            // Create line chart
            CategoryChart chart = createLineChart(viewModel);
            
            // XChartPanel handles display
            XChartPanel<CategoryChart> chartPanelComponent = new XChartPanel<>(chart);
            chartContainer.add(chartPanelComponent, BorderLayout.CENTER);
            
            chartContainer.revalidate();
            chartContainer.repaint();
        } catch (Exception e) {
            e.printStackTrace(); // Print error stack trace for debugging
            displayError(e.getMessage());
        }
    }

    public void displayError(String message) {
        chartContainer.removeAll();
        JLabel errorLabel = new JLabel("<html><center><p style='color:gray'>Data Unavailable</p></center></html>", SwingConstants.CENTER);
        chartContainer.add(errorLabel, BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    private CategoryChart createLineChart(ChartViewModel viewModel) {
        CategoryChart chart = new CategoryChartBuilder()
                .width(getWidth())
                .height(getHeight())
                .title(viewModel.getTitle()) 
                .build();

        // Apply styling
        ChartStyler.applyLineChartStyle(chart);

        List<String> labels = viewModel.getLabels();
        // For candlestick data, use Close Price to draw line chart
        List<Double> prices = viewModel.isCandlestick() ? viewModel.getClosePrices() : viewModel.getPrices();
        
        // --- 1. Data Sampling ---
        // Limit points to 90 to prevent overcrowding
        int maxPoints = 90; 
        
        List<String> displayLabels = new ArrayList<>();
        List<Double> displayPrices = new ArrayList<>();
        
        sampleData(labels, prices, displayLabels, displayPrices, maxPoints);

        // --- 2. Label Sparsification (Fixed Crash Issue) ---
        // Target: show approximately 8 dates on X-axis
        List<String> sparseLabels = sparsifyLabels(displayLabels, 8);

        // Add data series
        chart.addSeries("StockPrice", sparseLabels, displayPrices);
        
        return chart;
    }

    // Helper method: Data sampling
    private void sampleData(List<String> srcLabels, List<Double> srcPrices, 
                            List<String> destLabels, List<Double> destPrices, int maxPoints) {
        int dataSize = srcLabels.size();
        if (dataSize > maxPoints) {
            int step = (int) Math.ceil((double) dataSize / maxPoints);
            for (int i = 0; i < dataSize; i += step) {
                destLabels.add(srcLabels.get(i));
                destPrices.add(srcPrices.get(i));
            }
            // Ensure the last point is included
            if (dataSize > 0 && (dataSize - 1) % step != 0) {
                destLabels.add(srcLabels.get(dataSize - 1));
                destPrices.add(srcPrices.get(dataSize - 1));
            }
        } else {
            destLabels.addAll(srcLabels);
            destPrices.addAll(srcPrices);
        }
    }
    
    // Helper method: Label sparsification (Fixed BUG here)
    private List<String> sparsifyLabels(List<String> labels, int targetLabelCount) {
        List<String> result = new ArrayList<>();
        int step = Math.max(1, labels.size() / targetLabelCount);
        
        for (int i = 0; i < labels.size(); i++) {
            // Keep first, last, and labels matching the step
            if (i == 0 || i == labels.size() - 1 || i % step == 0) {
                String lbl = labels.get(i);
                // Simple truncation to show only MM-dd or HH:mm to avoid long text
                if (lbl.length() > 5) {
                   // You can customize based on your date format, e.g., take last 5 characters
                   result.add(lbl); 
                } else {
                   result.add(lbl);
                }
            } else {
                // !!! Critical Fix !!!
                // Do not use "" (empty string), use " " (space)
                // Java TextLayout cannot handle empty strings, will cause Crash
                result.add(" "); 
            }
        }
        return result;
    }
}
