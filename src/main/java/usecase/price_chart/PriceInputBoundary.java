package usecase.price_chart;
import entity.TimeInterval;
public interface PriceInputBoundary {
    void loadPriceHistory(String ticker, TimeInterval interval);
}
