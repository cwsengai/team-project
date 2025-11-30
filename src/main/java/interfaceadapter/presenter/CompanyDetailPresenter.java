package interfaceadapter.presenter;

import interfaceadapter.price_chart.CompanyDetailOutputBoundary;
import usecase.price_chart.PriceChartOutputBoundary;
import entity.*;
import framework_and_driver.CompanyDetailPage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CompanyDetailPresenter implements CompanyDetailOutputBoundary, PriceChartOutputBoundary {

    private final CompanyDetailPage view;

    public CompanyDetailPresenter(CompanyDetailPage view) {
        this.view = view;
    }

    @Override
    public void presentCompanyDetail(Company companyOverview, FinancialStatement financials, List<NewsArticle> news) {
        CompanyDetailViewModel viewModel = new CompanyDetailViewModel(companyOverview, financials, news);
        view.updateCompanyDetails(viewModel);
    }

    @Override
    public void presentError(String message) {
        // Show error in both dialog and chart area
        view.displayError(message);
        if (view.getChartPanel() != null) {
            view.getChartPanel().displayError(message);
        }
    }

    // Implement PriceChartOutputBoundary to update chart
    @Override
    public void presentPriceHistory(List<PricePoint> priceData, String ticker, TimeInterval interval) {
        if (priceData == null || priceData.isEmpty()) {
            presentError("No price data available");
            return;
        }

        List<String> labels = priceData.stream()
                .map(p -> formatTimestamp(p.getTimestamp(), interval))
                .collect(java.util.stream.Collectors.toList());

        // Check if we have complete OHLC data for candlestick chart
        boolean hasOHLCData = priceData.stream()
                .allMatch(p -> p.getOpen() != null && p.getHigh() != null && 
                              p.getLow() != null && p.getClose() != null);

        ChartViewModel viewModel;
        
        if (hasOHLCData && priceData.size() > 0) {
            // Create candlestick chart with OHLC data
            List<Double> openPrices = priceData.stream()
                    .map(p -> p.getOpen() != null ? p.getOpen() : 0.0)
                    .collect(java.util.stream.Collectors.toList());
            List<Double> highPrices = priceData.stream()
                    .map(p -> p.getHigh() != null ? p.getHigh() : 0.0)
                    .collect(java.util.stream.Collectors.toList());
            List<Double> lowPrices = priceData.stream()
                    .map(p -> p.getLow() != null ? p.getLow() : 0.0)
                    .collect(java.util.stream.Collectors.toList());
            List<Double> closePrices = priceData.stream()
                    .map(p -> p.getClose() != null ? p.getClose() : 0.0)
                    .collect(java.util.stream.Collectors.toList());

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
                    .collect(java.util.stream.Collectors.toList());

            viewModel = new ChartViewModel(ticker + " | " + interval.name(), labels, prices, interval);
        }

        view.updateChart(viewModel);
    }

    private String formatTimestamp(LocalDateTime timestamp, TimeInterval interval) {
        if (timestamp == null) {
            return "";
        }
        
        switch (interval) {
            case FIVE_MINUTES:
                return timestamp.format(DateTimeFormatter.ofPattern("HH:mm"));
            case DAILY:
                return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            case WEEKLY:
            case MONTHLY:
                return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            default:
                return timestamp.toString();
        }
    }
}
