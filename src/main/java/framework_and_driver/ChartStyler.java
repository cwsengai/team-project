package framework_and_driver;

import java.awt.Color;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.style.Styler;

/**
 * Utility class for applying consistent styling to XChart charts.
 */
public class ChartStyler {

    /**
     * Apply default styling to a chart.
     * This includes legend position, grid lines, background colors, and axis label rotation.
     * 
     * @param chart The chart to style
     */
    public static void applyDefaultStyle(CategoryChart chart) {
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setPlotGridLinesVisible(true);
        chart.getStyler().setPlotBackgroundColor(Color.WHITE);
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setAxisTickLabelsColor(Color.BLACK);
        chart.getStyler().setPlotGridLinesColor(new Color(220, 220, 220));
        chart.getStyler().setXAxisLabelRotation(0); // No rotation for cleaner look
        chart.getStyler().setPlotBorderVisible(false);
        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setChartTitleFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 16));
    }

    /**
     * Apply styling optimized for line charts (smooth, clean appearance)
     */
    public static void applyLineChartStyle(CategoryChart chart) {
        applyDefaultStyle(chart);
        chart.getStyler().setLegendVisible(false); // Hide legend for cleaner line charts
        chart.getStyler().setMarkerSize(0); // No markers for smooth line
        // Note: Line width is set when adding series, not in styler
    }

    /**
     * Apply styling for candlestick charts
     */
    public static void applyCandlestickStyle(CategoryChart chart) {
        applyDefaultStyle(chart);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);
        chart.getStyler().setMarkerSize(4); // Smaller markers for candlestick
    }
}

