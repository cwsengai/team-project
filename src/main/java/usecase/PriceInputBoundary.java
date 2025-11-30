package usecase;
import entity.TimeInterval;
public interface PriceInputBoundary {
    void loadPriceHistory(String ticker, TimeInterval interval);
}
