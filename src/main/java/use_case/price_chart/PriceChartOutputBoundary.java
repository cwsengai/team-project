package use_case.price_chart;
import java.util.List;

import entity.PricePoint;
import entity.TimeInterval;
public interface PriceChartOutputBoundary {
    void presentPriceHistory(List<PricePoint> priceData, String ticker, TimeInterval interval);
    void presentError(String message);
}