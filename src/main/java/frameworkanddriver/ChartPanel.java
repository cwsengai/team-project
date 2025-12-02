package frameworkanddriver;

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

import dataaccess.AlphaVantagePriceGateway;
import entity.ChartViewModel;
import entity.TimeInterval;
import interfaceadapter.controller.IntervalController;
import interfaceadapter.presenter.PriceChartPresenter;
import usecase.price_chart.GetPriceByIntervalInteractor;
import usecase.price_chart.PriceChartOutputBoundary;
import usecase.price_chart.PriceDataAccessInterface;

@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:SuppressWarnings"})
public class ChartPanel extends JPanel {

    private static final String EMPTY_STRING = " ";
    private static final int FONT_SIZE_MONOSPACED = 12;
    private static final int BORDER_TOP = 5;
    private static final int BORDER_LEFT = 10;
    private static final int BORDER_BOTTOM = 5;
    private static final int BORDER_RIGHT = 10;
    private static final int PERCENTAGE_MULTIPLIER = 100;
    private static final int COLOR_GREEN_R = 0;
    private static final int COLOR_GREEN_G = 150;
    private static final int COLOR_GREEN_B = 0;
    private static final int COLOR_RED_R = 200;
    private static final int COLOR_RED_G = 0;
    private static final int COLOR_RED_B = 0;
    private static final int FONT_SIZE_SANS_SERIF = 12;
    private static final int TOOLTIP_ALPHA_R = 255;
    private static final int TOOLTIP_ALPHA_G = 255;
    private static final int TOOLTIP_ALPHA_B = 255;
    private static final int TOOLTIP_ALPHA_A = 230;
    private static final int MAX_POINTS = 90;
    private static final int TARGET_LABEL_COUNT_FIVE_MIN = 12;
    private static final int TARGET_LABEL_COUNT_OTHER = 8;
    private static final int MIN_LENGTH_FOR_TIME = 16;
    private static final int TIME_SUBSTRING_START = 1;
    private static final int TIME_SUBSTRING_END = 6;
    private static final int DATE_SUBSTRING_START = 5;
    private static final int DATE_SUBSTRING_END = 10;
    private static final int MIN_LENGTH_FOR_DATE = 10;
    private static final int GRAY_COLOR_R = 80;
    private static final int GRAY_COLOR_G = 80;
    private static final int GRAY_COLOR_B = 80;

    private final JPanel chartContainer;
    private final JLabel infoLabel;
    private String linkedTicker;

    public ChartPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        Color bgColor = Color.WHITE;
        setBackground(bgColor);

        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(bgColor);

        final JLabel placeholder = new JLabel("Waiting for data...", SwingConstants.CENTER);
        chartContainer.add(placeholder, BorderLayout.CENTER);
        add(chartContainer, BorderLayout.CENTER);

        infoLabel = new JLabel(EMPTY_STRING);
        infoLabel.setFont(new Font("Monospaced", Font.PLAIN, FONT_SIZE_MONOSPACED));
        Color textColor = new Color(GRAY_COLOR_R, GRAY_COLOR_G, GRAY_COLOR_B);
        infoLabel.setForeground(textColor);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(
            BORDER_TOP, BORDER_LEFT, BORDER_BOTTOM, BORDER_RIGHT));
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
     * Enable zoom functionality.
     *
     * @param ticker the ticker symbol to enable zoom for
     */
    public void enableZoom(String ticker) {
        this.linkedTicker = ticker;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chartContainer.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Public method: Manually trigger zoom. Called by external button (Zoom in).
     */
    public void performZoom() {
        if (linkedTicker != null) {
            openZoomWindow(linkedTicker);
        }
        else {
            System.out.println("No ticker selected for zoom.");
        }
    }

    @SuppressWarnings({"checkstyle:LambdaBodyLength", "checkstyle:IllegalCatch"})
    private void openZoomWindow(String ticker) {
        SwingUtilities.invokeLater(() -> {
            try {
                final PriceDataAccessInterface priceGateway = new AlphaVantagePriceGateway();
                final ChartWindow zoomWindow = new ChartWindow();
                zoomWindow.setTitle("Market Detail: " + ticker);
                final PriceChartOutputBoundary pricePresenter =
                    new PriceChartPresenter(zoomWindow);
                final GetPriceByIntervalInteractor interactor =
                    new GetPriceByIntervalInteractor(priceGateway, pricePresenter);
                final IntervalController intervalController =
                    new IntervalController(interactor);
                zoomWindow.setController(intervalController);
                zoomWindow.setVisible(true);
                intervalController.handleTimeChange("1D");
            }
            catch (RuntimeException runtimeException) {
                System.err.println("ChartPanel openZoomWindow runtime error: " + runtimeException.getMessage());
                for (StackTraceElement ste : runtimeException.getStackTrace()) {
                    System.err.println("    at " + ste.toString());
                }
            }
        });
    }

    /**
     * Updates the chart with the provided view model data.
     *
     * @param viewModel the chart view model containing data to display
     */
    @SuppressWarnings("checkstyle:IllegalCatch")
    public void updateChart(ChartViewModel viewModel) {
        chartContainer.removeAll();
        try {
            final CategoryChart chart = createLineChart(viewModel);
            updateInfoLabel(viewModel);

            final XChartPanel<CategoryChart> chartPanelComponent =
                new XChartPanel<>(chart);

            chartPanelComponent.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent mouseEvent) {
                    if (linkedTicker != null) {
                        openZoomWindow(linkedTicker);
                    }
                }
            });

            if (linkedTicker != null) {
                chartPanelComponent.setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            chartContainer.add(chartPanelComponent, BorderLayout.CENTER);
            chartContainer.revalidate();
            chartContainer.repaint();
        }
        catch (RuntimeException runtimeException) {
            System.err.println("ChartPanel updateChart runtime error: " + runtimeException.getMessage());
            for (StackTraceElement ste : runtimeException.getStackTrace()) {
                System.err.println("    at " + ste.toString());
            }
            displayError(runtimeException.getMessage());
        }
    }

    private void updateInfoLabel(ChartViewModel viewModel) {
        final List<Double> prices;
        if (viewModel.isCandlestick()) {
            prices = viewModel.getClosePrices();
        }
        else {
            prices = viewModel.getPrices();
        }

        if (prices != null && !prices.isEmpty()) {
            final double currentPrice = prices.get(prices.size() - 1);
            double startPrice = prices.get(0);
            if (prices.size() > 1) {
                startPrice = prices.get(prices.size() - 2);
            }

            final double change = currentPrice - startPrice;
            final double changePercent = (change / startPrice) * PERCENTAGE_MULTIPLIER;

            final DecimalFormat df = new DecimalFormat("0.00");
            final String sign;
            if (change >= 0) {
                sign = "+";
            }
            else {
                sign = "";
            }

            final String infoText = String.format("Close: %s   Chg: %s%s (%s%s%%)",
                    df.format(currentPrice),
                    sign, df.format(change),
                    sign, df.format(changePercent)
            );

            infoLabel.setText(infoText);
            if (change >= 0) {
                infoLabel.setForeground(new Color(
                    COLOR_GREEN_R, COLOR_GREEN_G, COLOR_GREEN_B));
            }
            else {
                infoLabel.setForeground(new Color(
                    COLOR_RED_R, COLOR_RED_G, COLOR_RED_B));
            }
        }
        else {
            infoLabel.setText("No Data");
        }
    }

    /**
     * Displays an error message on the chart panel.
     *
     * @param message the error message to display
     */
    public void displayError(String message) {
        chartContainer.removeAll();
        String displayMsg = message;
        if (message != null && message.contains("Series data columns")) {
            displayMsg = "API Limit Reached (Wait 1 min)";
        }
        final String errorHtml = "<html><center><p style='color:gray; " + "font-size:14px'>Warning: " + displayMsg
            + "</p></center></html>";
        final JLabel errorLabel = new JLabel(errorHtml, SwingConstants.CENTER);
        chartContainer.add(errorLabel, BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
        infoLabel.setText(EMPTY_STRING);
    }

    private CategoryChart createLineChart(ChartViewModel viewModel) {
        final CategoryChart chart = new CategoryChartBuilder()
                .width(getWidth())
                .height(getHeight())
                .title(viewModel.getTitle())
                .build();

        ChartStyler.applyLineChartStyle(chart);

        chart.getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
        chart.getStyler().setToolTipFont(
            new Font("SansSerif", Font.PLAIN, FONT_SIZE_SANS_SERIF));
        chart.getStyler().setToolTipBackgroundColor(new Color(
            TOOLTIP_ALPHA_R, TOOLTIP_ALPHA_G, TOOLTIP_ALPHA_B, TOOLTIP_ALPHA_A));
        chart.getStyler().setToolTipBorderColor(Color.LIGHT_GRAY);

        final List<String> labels = viewModel.getLabels();
        final List<Double> prices;
        if (viewModel.isCandlestick()) {
            prices = viewModel.getClosePrices();
        }
        else {
            prices = viewModel.getPrices();
        }
        final TimeInterval interval = viewModel.getInterval();

        final List<String> sampledLabels = new ArrayList<>();
        final List<Double> sampledPrices = new ArrayList<>();
        sampleData(labels, prices, sampledLabels, sampledPrices, MAX_POINTS);

        final List<String> formattedLabels = new ArrayList<>();
        for (String raw : sampledLabels) {
            formattedLabels.add(formatLabel(raw, interval));
        }

        final int targetLabelCount;
        if (interval == TimeInterval.FIVE_MINUTES) {
            targetLabelCount = TARGET_LABEL_COUNT_FIVE_MIN;
        }
        else {
            targetLabelCount = TARGET_LABEL_COUNT_OTHER;
        }
        final List<String> finalLabels =
            sparsifyLabels(formattedLabels, targetLabelCount);

        chart.addSeries("StockPrice", finalLabels, sampledPrices);
        return chart;
    }

    private String formatLabel(String raw, TimeInterval interval) {
        final String formattedLabel;
        if (raw == null || raw.isEmpty()) {
            formattedLabel = "";
        }
        else if (interval == TimeInterval.FIVE_MINUTES) {
            formattedLabel = formatTimeLabel(raw);
        }
        else {
            formattedLabel = formatDateLabel(raw);
        }
        return formattedLabel;
    }

    private String formatTimeLabel(String raw) {
        String result = null;
        if (raw.length() >= MIN_LENGTH_FOR_TIME) {
            final int tIndex = raw.indexOf('T');
            final int spaceIndex = raw.indexOf(' ');
            final int separatorIndex;
            if (tIndex != -1) {
                separatorIndex = tIndex;
            }
            else {
                separatorIndex = spaceIndex;
            }

            if (separatorIndex != -1 && separatorIndex + TIME_SUBSTRING_END <= raw.length()) {
                result = raw.substring(
                        separatorIndex + TIME_SUBSTRING_START,
                        separatorIndex + TIME_SUBSTRING_END);
            }
        }
        if (result == null) {
            result = raw;
        }
        return result;
    }

    private String formatDateLabel(String raw) {
        String result = raw;
        if (raw.length() >= MIN_LENGTH_FOR_DATE) {
            result = raw.substring(DATE_SUBSTRING_START, DATE_SUBSTRING_END);
        }
        return result;
    }

    private void sampleData(List<String> srcLabels, List<Double> srcPrices,
                            List<String> destLabels, List<Double> destPrices,
                            int maxPoints) {
        final int dataSize = srcLabels.size();
        if (dataSize > maxPoints) {
            final int step = (int) Math.ceil((double) dataSize / maxPoints);
            for (int i = 0; i < dataSize; i += step) {
                destLabels.add(srcLabels.get(i));
                destPrices.add(srcPrices.get(i));
            }
            if (dataSize > 0 && (dataSize - 1) % step != 0) {
                destLabels.add(srcLabels.get(dataSize - 1));
                destPrices.add(srcPrices.get(dataSize - 1));
            }
        }
        else {
            destLabels.addAll(srcLabels);
            destPrices.addAll(srcPrices);
        }
    }

    private List<String> sparsifyLabels(List<String> labels, int targetLabelCount) {
        final List<String> result = new ArrayList<>();
        final int step = Math.max(1, labels.size() / targetLabelCount);
        for (int i = 0; i < labels.size(); i++) {
            if (i == 0 || i == labels.size() - 1 || i % step == 0) {
                result.add(labels.get(i));
            }
            else {
                result.add(EMPTY_STRING);
            }
        }
        return result;
    }
}
