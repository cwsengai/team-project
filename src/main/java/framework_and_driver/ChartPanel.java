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

    private final Color TEXT_COLOR = new Color(80, 80, 80);
    private final Color BG_COLOR = Color.WHITE;

    public ChartPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 
        setBackground(BG_COLOR);
        
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(BG_COLOR);
        
        JLabel placeholder = new JLabel("Waiting for data...", SwingConstants.CENTER);
        chartContainer.add(placeholder, BorderLayout.CENTER);
        add(chartContainer, BorderLayout.CENTER);

        infoLabel = new JLabel(" ");
        infoLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        infoLabel.setForeground(TEXT_COLOR);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        infoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(infoLabel, BorderLayout.SOUTH);

        chartContainer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (linkedTicker != null) {
                    openZoomWindow(linkedTicker);
                }
            }
        });
    }

    public void enableZoom(String ticker) {
        this.linkedTicker = ticker;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chartContainer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
                    if (linkedTicker != null) {
                        openZoomWindow(linkedTicker);
                    }
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
            
            if (prices.size() > 1) {
                startPrice = prices.get(prices.size() - 2);
            }
            
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
        JLabel errorLabel = new JLabel("<html><center><p style='color:gray'>Data Unavailable</p></center></html>", SwingConstants.CENTER);
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
        
        int maxPoints = 90; 
        List<String> displayLabels = new ArrayList<>();
        List<Double> displayPrices = new ArrayList<>();
        sampleData(labels, prices, displayLabels, displayPrices, maxPoints);

        List<String> sparseLabels = sparsifyLabels(displayLabels, 8); 

        chart.addSeries("StockPrice", sparseLabels, displayPrices);
        return chart;
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
                String lbl = labels.get(i);
                if (lbl.length() > 5) result.add(lbl); 
                else result.add(lbl);
            } else {
                result.add(" "); 
            }
        }
        return result;
    }
}
