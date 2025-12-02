package interfaceadapter.presenter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import entity.ChartViewModel;
import entity.PricePoint;
import entity.TimeInterval;
import frameworkanddriver.ChartWindow;
import usecase.price_chart.PriceChartOutputBoundary;

public record PriceChartPresenter(ChartWindow view) implements PriceChartOutputBoundary {

    @Override
    public void presentPriceHistory(List<PricePoint> priceData, String ticker, TimeInterval interval) {
        if (priceData == null || priceData.isEmpty()) {
            presentError("No price data available");
            return;
        }

        List<String> labels = priceData.stream()
                .map(p -> formatTimestamp(p.timestamp(), interval))
                .collect(Collectors.toList());

        // Check if we have complete OHLC data for candlestick chart
        boolean hasOHLCData = priceData.stream()
                .allMatch(p -> p.open() != null && p.high() != null
                        && p.low() != null && p.close() != null);

        ChartViewModel viewModel;

        if (hasOHLCData && !priceData.isEmpty()) {
            // Create candlestick chart with OHLC data
            List<Double> closePrices = priceData.stream()
                    .map(p -> p.close() != null ? p.close() : 0.0)
                    .collect(Collectors.toList());

            viewModel = new ChartViewModel(
                    ticker + " | " + interval.name(),
                    labels,
                    closePrices, interval
            );
        } else {
            // Create simple line chart with close prices
            List<Double> prices = priceData.stream()
                    .map(p -> p.close() != null ? p.close()
                            :
                            (p.getPrice() != 0.0 ? p.getPrice() : 0.0))
                    .collect(Collectors.toList());

            viewModel = new ChartViewModel(ticker + " | " + interval.name(), labels, prices, interval);
        }

        view.updateChart(viewModel);
    }

    private String formatTimestamp(LocalDateTime timestamp, TimeInterval interval) {
        if (timestamp == null) {
            return "";
        }

        // Format based on interval for better readability
        return switch (interval) {
            case FIVE_MINUTES -> timestamp.format(DateTimeFormatter.ofPattern("HH:mm"));
            case DAILY -> timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            case WEEKLY, MONTHLY -> timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            default -> timestamp.toString();
        };
    }

    @Override
    public void presentError(String message) {
        view.displayError(message);
    }
}
