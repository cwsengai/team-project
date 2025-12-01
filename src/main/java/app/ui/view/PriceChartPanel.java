package app.ui.view;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;

public class PriceChartPanel extends JPanel {
    private List<Double> priceHistory = new ArrayList<>();

    public PriceChartPanel() {
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    }

    /**
     * Updates the price history with the provided list of prices and repaints the component.
     *
     * @param newPrices the new list of price values to display
     */
    public void updateData(List<Double> newPrices) {
        // Copy data to avoid concurrency issues
        this.priceHistory = new ArrayList<>(newPrices);
        // Triggers paintComponent
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int width = getWidth();
        final int height = getHeight();
        final int padding = 30;

        if (priceHistory == null || priceHistory.size() < 2) {
            g2.drawString("Waiting for market data...", width / 2 - 60, height / 2);
            return;
        }

        // 1. Determine Y-axis range
        final double minPrice = Collections.min(priceHistory);
        final double maxPrice = Collections.max(priceHistory);
        double range = maxPrice - minPrice;
        if (range == 0) {
            range = 1.0;
        }

        // 2. Calculate scales
        final double xScale = (double) (width - 2 * padding) / (priceHistory.size() - 1);
        final double yScale = (double) (height - 2 * padding) / range;

        // 3. Draw the Line Chart
        // Red color for finance
        g2.setColor(new Color(235, 77, 75));
        g2.setStroke(new BasicStroke(2f));

        for (int i = 0; i < priceHistory.size() - 1; i++) {
            final int x1 = (int) (i * xScale + padding);
            final int y1 = (int) ((maxPrice - priceHistory.get(i)) * yScale + padding);

            final int x2 = (int) ((i + 1) * xScale + padding);
            final int y2 = (int) ((maxPrice - priceHistory.get(i + 1)) * yScale + padding);

            g2.drawLine(x1, y1, x2, y2);
        }

        // 4. Draw current price label
        g2.setColor(Color.BLACK);
        final double lastPrice = priceHistory.get(priceHistory.size() - 1);
        g2.drawString(String.format("Current: %.2f", lastPrice), width - 120, 20);
    }
}
