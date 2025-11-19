package data_access.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import entity.PricePoint;
import entity.TimeInterval;

/**
 * Repository interface for PricePoint entity persistence.
 * Manages historical and real-time price data.
 */
public interface PriceRepository {
    void savePricePoint(PricePoint pricePoint);

    void savePricePoints(List<PricePoint> pricePoints);

    Optional<PricePoint> getLatestPrice(String ticker, TimeInterval interval);

    Map<String, PricePoint> getLatestPrices(List<String> tickers);

    List<PricePoint> getHistoricalPrices(String ticker, LocalDateTime start, LocalDateTime end, TimeInterval interval);

    void cleanup(LocalDateTime olderThan);
}
