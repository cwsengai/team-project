package framework_and_driver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;

import entity.ChartViewModel;
import entity.TimeInterval; // Must import this

// --- Dependencies ---
import api.AlphaVantagePriceGateway;
import interface_adapter.IntervalController;
import interface_adapter.PriceChartPresenter;
import use_case.GetPriceByIntervalInteractor;
import use_case.PriceChartOutputBoundary;
import use_case.PriceDataAccessInterface;

public class ChartPanel extends JPanel {

    private JPanel chartContainer;
    private JLabel infoLabel;
    private String linkedTicker = null;

    // Color constants
    private final Color TEXT_COLOR = new Color(80, 80, 80);
    private final Color BG_COLOR = Color.WHITE;

    public ChartPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 
        setBackground(BG_COLOR);
        
        // 1. Chart area
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(BG_COLOR);
        
        JLabel placeholder = new JLabel("Waiting for data...", SwingConstants.CENTER);
        chartContainer.add(placeholder, BorderLayout.CENTER);
        add(chartContainer, BorderLayout.CENTER);

        // 2. Bottom info bar
        infoLabel = new JLabel(" ");
        infoLabel.setFont(new Font("Monospaced", Font.PLAIN, 12)); 
        infoLabel.setForeground(TEXT_COLOR);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        infoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(infoLabel, BorderLayout.SOUTH);

        // Click listener
        chartContainer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (linkedTicker != null) {
                    openZoomWindow(linkedTicker);
                }
            }
        });
    }

    /**
     * Enable zoom functionality
     */
    public void enableZoom(String ticker) {
        this.linkedTicker = ticker;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chartContainer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    
    /**
     * [New] Public method: Manually trigger zoom
     * Called by external button (Zoom in)
     */
    public void performZoom() {
        if (linkedTicker != null) {
            openZoomWindow(linkedTicker);
        } else {
            System.out.println("No ticker selected for zoom.");
        }
    }

    private void openZoomWindow(String ticker) {
        SwingUtilities.invokeLater(() -> {
            try {
                PriceDataAccessInterface priceGateway = new AlphaVantagePriceGateway();
                ChartWindow zoomWindow = new ChartWindow();
                zoomWindow.setTitle("Market Detail: " + ticker);
                PriceChartOutputBoundary pricePresenter = new PriceChartPresenter(zoomWindow);
                GetPriceByIntervalInteractor interactor = new GetPriceByIntervalInteractor(priceGateway, pricePresenter);
                IntervalController intervalController = new IntervalController(interactor);
                zoomWindow.setController(intervalController);
                zoomWindow.setVisible(true);
                // Default load 1D
                intervalController.handleTimeChange("1D"); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void updateChart(ChartViewModel viewModel) {
        chartContainer.removeAll();
        try {
            CategoryChart chart = createLineChart(viewModel);
            updateInfoLabel(viewModel);
            
            XChartPanel<CategoryChart> chartPanelComponent = new XChartPanel<>(chart);
            
            chartPanelComponent.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (linkedTicker != null) openZoomWindow(linkedTicker);
                }
            });
            
            if (linkedTicker != null) {
                chartPanelComponent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            
            chartContainer.add(chartPanelComponent, BorderLayout.CENTER);
            chartContainer.revalidate();
            chartContainer.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            displayError(e.getMessage());
        }
    }
    
    private void updateInfoLabel(ChartViewModel viewModel) {
        List<Double> prices = viewModel.isCandlestick() ? viewModel.getClosePrices() : viewModel.getPrices();
        
        if (prices != null && !prices.isEmpty()) {
            double currentPrice = prices.get(prices.size() - 1);
            double startPrice = prices.get(0);
            if (prices.size() > 1) startPrice = prices.get(prices.size() - 2);
            
            double change = currentPrice - startPrice;
            double changePercent = (change / startPrice) * 100;

            DecimalFormat df = new DecimalFormat("0.00");
            String sign = change >= 0 ? "+" : "";
            
            String infoText = String.format("Close: %s   Chg: %s%s (%s%s%%)", 
                    df.format(currentPrice),
                    sign, df.format(change),
                    sign, df.format(changePercent)
            );
            
            infoLabel.setText(infoText);
            if (change >= 0) infoLabel.setForeground(new Color(0, 150, 0));
            else infoLabel.setForeground(new Color(200, 0, 0));
        } else {
            infoLabel.setText("No Data");
        }
    }

    public void displayError(String message) {
        chartContainer.removeAll();
        String displayMsg = message;
        if (message != null && message.contains("Series data columns")) {
            displayMsg = "API Limit Reached (Wait 1 min)";
        }
        JLabel errorLabel = new JLabel("<html><center><p style='color:gray; font-size:14px'>⚠️ " + displayMsg + "</p></center></html>", SwingConstants.CENTER);
        chartContainer.add(errorLabel, BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
        infoLabel.setText(" ");
    }

    private CategoryChart createLineChart(ChartViewModel viewModel) {
        CategoryChart chart = new CategoryChartBuilder()
                .width(getWidth())
                .height(getHeight())
                .title(viewModel.getTitle()) 
                .build();

        ChartStyler.applyLineChartStyle(chart);
        
        chart.getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
        chart.getStyler().setToolTipFont(new Font("SansSerif", Font.PLAIN, 12));
        chart.getStyler().setToolTipBackgroundColor(new Color(255, 255, 255, 230)); 
        chart.getStyler().setToolTipBorderColor(Color.LIGHT_GRAY);

        List<String> labels = viewModel.getLabels();
        List<Double> prices = viewModel.isCandlestick() ? viewModel.getClosePrices() : viewModel.getPrices();
        TimeInterval interval = viewModel.getInterval();
        
        // 1. Data sampling (prevent too many points)
        int maxPoints = 90; 
        List<String> sampledLabels = new ArrayList<>();
        List<Double> sampledPrices = new ArrayList<>();
        sampleData(labels, prices, sampledLabels, sampledPrices, maxPoints);

        // 2. Label formatting (5M shows time, 1D shows date)
        List<String> formattedLabels = new ArrayList<>();
        for (String raw : sampledLabels) {
            formattedLabels.add(formatLabel(raw, interval));
        }

        // 3. Label sparsification (prevent text overlap)
        // If 5M (short text), can show more (12 labels)
        // If 1D/1W (long text), show fewer (8 labels)
        int targetLabelCount = (interval == TimeInterval.FIVE_MINUTES) ? 12 : 8;
        List<String> finalLabels = sparsifyLabels(formattedLabels, targetLabelCount); 

        chart.addSeries("StockPrice", finalLabels, sampledPrices);
        return chart;
    }

    // --- New: Smart label formatting ---
    private String formatLabel(String raw, TimeInterval interval) {
        if (raw == null || raw.isEmpty()) return "";
        
        // Original format is usually: "2025-11-24T13:30:00" or "2025-11-24"
        // We decide which part to keep based on Interval
        
        if (interval == TimeInterval.FIVE_MINUTES) {
            // 5M: Only want "HH:mm" (e.g., 13:30)
            // Assume raw contains 'T' or space separator
            if (raw.length() >= 16) {
                // Find position of T or space
                int tIndex = raw.indexOf('T');
                if (tIndex == -1) tIndex = raw.indexOf(' ');
                
                if (tIndex != -1 && tIndex + 6 <= raw.length()) {
                    return raw.substring(tIndex + 1, tIndex + 6); // Extract HH:mm
                }
            }
            return raw; // If format is wrong, return as is
            
        } else {
            // 1D / 1W: Only want "MM-dd" (e.g., 11-24)
            if (raw.length() >= 10) {
                return raw.substring(5, 10); // Extract MM-dd
            }
            return raw;
        }
    }

    private void sampleData(List<String> srcLabels, List<Double> srcPrices, 
                            List<String> destLabels, List<Double> destPrices, int maxPoints) {
        int dataSize = srcLabels.size();
        if (dataSize > maxPoints) {
            int step = (int) Math.ceil((double) dataSize / maxPoints);
            for (int i = 0; i < dataSize; i += step) {
                destLabels.add(srcLabels.get(i));
                destPrices.add(srcPrices.get(i));
            }
            if (dataSize > 0 && (dataSize - 1) % step != 0) {
                destLabels.add(srcLabels.get(dataSize - 1));
                destPrices.add(srcPrices.get(dataSize - 1));
            }
        } else {
            destLabels.addAll(srcLabels);
            destPrices.addAll(srcPrices);
        }
    }
    
    private List<String> sparsifyLabels(List<String> labels, int targetLabelCount) {
        List<String> result = new ArrayList<>();
        int step = Math.max(1, labels.size() / targetLabelCount);
        for (int i = 0; i < labels.size(); i++) {
            if (i == 0 || i == labels.size() - 1 || i % step == 0) {
                result.add(labels.get(i));
            } else {
                result.add(" "); 
            }
        }
        return result;
    }
}
