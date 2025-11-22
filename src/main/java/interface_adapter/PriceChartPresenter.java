package interface_adapter;

import java.util.List;
import java.util.stream.Collectors;

import entity.ChartViewModel;
import entity.PricePoint;
import entity.TimeInterval;
import framework_and_driver.ChartWindow;
import use_case.PriceChartOutputBoundary;

public class PriceChartPresenter implements PriceChartOutputBoundary {

    private final ChartWindow view;

    public PriceChartPresenter(ChartWindow view) {
        this.view = view;
    }

    @Override
    public void presentPriceHistory(List<PricePoint> priceData, String ticker, TimeInterval interval) {
        if (priceData == null || priceData.isEmpty()) {
            presentError("No price data available");
            return;
        }

        List<String> labels = priceData.stream()
                .map(p -> formatTimestamp(p.getTimestamp(), interval))
                .collect(Collectors.toList());

        // Check if we have complete OHLC data for candlestick chart
        boolean hasOHLCData = priceData.stream()
                .allMatch(p -> p.getOpen() != null && p.getHigh() != null && 
                              p.getLow() != null && p.getClose() != null);

        ChartViewModel viewModel;
        
        if (hasOHLCData && priceData.size() > 0) {
            // Create candlestick chart with OHLC data
            List<Double> openPrices = priceData.stream()
                    .map(p -> p.getOpen() != null ? p.getOpen() : 0.0)
                    .collect(Collectors.toList());
            List<Double> highPrices = priceData.stream()
                    .map(p -> p.getHigh() != null ? p.getHigh() : 0.0)
                    .collect(Collectors.toList());
            List<Double> lowPrices = priceData.stream()
                    .map(p -> p.getLow() != null ? p.getLow() : 0.0)
                    .collect(Collectors.toList());
            List<Double> closePrices = priceData.stream()
                    .map(p -> p.getClose() != null ? p.getClose() : 0.0)
                    .collect(Collectors.toList());

            viewModel = new ChartViewModel(
                    ticker + " | " + interval.name(), 
                    labels, 
                    openPrices, highPrices, lowPrices, closePrices, interval
            );
        } else {
            // Create simple line chart with close prices
            List<Double> prices = priceData.stream()
                    .map(p -> p.getClose() != null ? p.getClose() : 
                             (p.getPrice() != 0.0 ? p.getPrice() : 0.0))
                    .collect(Collectors.toList());

            viewModel = new ChartViewModel(ticker + " | " + interval.name(), labels, prices, interval);
        }

        view.updateChart(viewModel);
    }

    private String formatTimestamp(java.time.LocalDateTime timestamp, TimeInterval interval) {
        if (timestamp == null) {
            return "";
        }
        
        // Format based on interval for better readability
        switch (interval) {
            case INTRADAY:
                return timestamp.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            case DAILY:
                return timestamp.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            case WEEKLY:
            case MONTHLY:
                return timestamp.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
            default:
                return timestamp.toString();
        }
    }

    @Override
    public void presentError(String message) {
        view.displayError(message);
    }
}