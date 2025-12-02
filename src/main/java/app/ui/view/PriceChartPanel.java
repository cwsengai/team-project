package app.ui.view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PriceChartPanel extends JPanel {
    private List<Double> priceHistory = new ArrayList<>();

    public PriceChartPanel() {
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    }

    @SuppressWarnings({"checkstyle:TrailingComment", "checkstyle:MissingJavadocMethod"})
    public void updateData(List<Double> newPrices) {
        // Copy data to avoid concurrency issues
        this.priceHistory = new ArrayList<>(newPrices);
        this.repaint(); // Triggers paintComponent
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:FinalLocalVariable", "checkstyle:TrailingComment", "checkstyle:NeedBraces", "checkstyle:ReturnCount"})
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 30;

        if (priceHistory == null || priceHistory.size() < 2) {
            g2.drawString("Waiting for market data...", width / 2 - 60, height / 2);
            return;
        }

        // 1. Determine Y-axis range
        double minPrice = Collections.min(priceHistory);
        double maxPrice = Collections.max(priceHistory);
        double range = maxPrice - minPrice;
        if (range == 0) range = 1.0;

        // 2. Calculate scales
        double xScale = (double) (width - 2 * padding) / (priceHistory.size() - 1);
        double yScale = (double) (height - 2 * padding) / range;

        // 3. Draw the Line Chart
        g2.setColor(new Color(235, 77, 75)); // Red color for finance
        g2.setStroke(new BasicStroke(2f));

        for (int i = 0; i < priceHistory.size() - 1; i++) {
            int x1 = (int) (i * xScale + padding);
            int y1 = (int) ((maxPrice - priceHistory.get(i)) * yScale + padding);

            int x2 = (int) ((i + 1) * xScale + padding);
            int y2 = (int) ((maxPrice - priceHistory.get(i + 1)) * yScale + padding);

            g2.drawLine(x1, y1, x2, y2);
        }

        // 4. Draw current price label
        g2.setColor(Color.BLACK);
        double lastPrice = priceHistory.get(priceHistory.size() - 1);
        g2.drawString(String.format("Current: %.2f", lastPrice), width - 120, 20);
    }
}