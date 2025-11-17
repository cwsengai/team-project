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
        List<String> labels = priceData.stream()
                .map(p -> p.getTimestamp().toString())
                .collect(Collectors.toList());

        List<Double> prices = priceData.stream()
                .map(p -> p.getClose() != null ? p.getClose() : 0.0)
                .collect(Collectors.toList());

        ChartViewModel viewModel = new ChartViewModel(ticker + " | " + interval.name(), labels, prices);

        view.updateChart(viewModel);
    }

    @Override
    public void presentError(String message) {
        view.displayError(message);
    }
}