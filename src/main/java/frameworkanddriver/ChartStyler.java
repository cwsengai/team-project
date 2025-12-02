package frameworkanddriver;

import java.awt.Color;
import java.awt.Font;

import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategorySeries.CategorySeriesRenderStyle;

/**
 * Utility class for applying "Billionaire" UI style to XChart.
 */
public class ChartStyler {

    // Color definitions: Finance blue, white background, dark gray text
    public static final Color FINANCE_BLUE = new Color(0, 122, 255);
    public static final Color BG_COLOR = Color.WHITE;
    public static final Color GRID_COLOR = new Color(245, 245, 245);
    public static final Color TEXT_COLOR = new Color(80, 80, 80);

    // Magic Numbers extracted as constants
    private static final int AXIS_FONT_SIZE = 11;
    private static final double PLOT_CONTENT_SIZE = 0.95;

    @SuppressWarnings({"checkstyle:MissingJavadocMethod", "checkstyle:SuppressWarnings"})
    public static void applyDefaultStyle(CategoryChart chart) {
        // 1. Base background
        chart.getStyler().setChartBackgroundColor(BG_COLOR);
        chart.getStyler().setPlotBackgroundColor(BG_COLOR);

        // 2. Hide extra borders and legend (Clean Look)
        chart.getStyler().setChartTitleVisible(false);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setPlotBorderVisible(false);

        // 3. Axis styling
        chart.getStyler().setAxisTickLabelsColor(TEXT_COLOR);
        // Replaced 11 with AXIS_FONT_SIZE
        chart.getStyler().setAxisTickLabelsFont(new Font("SansSerif", Font.PLAIN, AXIS_FONT_SIZE));

        // Hide axis titles (X: Date, Y: Price) for minimalism
        chart.getStyler().setXAxisTitleVisible(false);
        chart.getStyler().setYAxisTitleVisible(false);

        // 4. Grid lines - Only keep horizontal grid lines for easier price reading
        chart.getStyler().setPlotGridLinesVisible(true);
        chart.getStyler().setPlotGridLinesColor(GRID_COLOR);
        chart.getStyler().setPlotGridVerticalLinesVisible(false);
        chart.getStyler().setPlotGridHorizontalLinesVisible(true);
    }

    @SuppressWarnings({"checkstyle:MissingJavadocMethod", "checkstyle:SuppressWarnings"})
    public static void applyLineChartStyle(CategoryChart chart) {
        applyDefaultStyle(chart);

        // --- Core Fix ---
        // Force line chart mode (default is Bar)
        chart.getStyler().setDefaultSeriesRenderStyle(CategorySeriesRenderStyle.Line);

        // Ensure Y-axis auto-scaling (do not force start from 0)
        chart.getStyler().setYAxisMin(null);
        chart.getStyler().setYAxisMax(null);

        // Line styling
        chart.getStyler().setMarkerSize(0);
        // Remove data point markers for smooth line
        chart.getStyler().setSeriesColors(new Color[] {FINANCE_BLUE });
        // Force blue color

        // Set chart content size
        // Replaced 0.95 with PLOT_CONTENT_SIZE
        chart.getStyler().setPlotContentSize(PLOT_CONTENT_SIZE);
        // Chart fill ratio
    }

}
