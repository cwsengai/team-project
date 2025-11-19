package data_access.repository.supabase;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import data_access.client.SupabaseClient;
import data_access.exception.DatabaseConnectionException;
import data_access.exception.PermissionDeniedException;
import data_access.exception.RepositoryException;
import data_access.repository.PriceRepository;
import entity.PricePoint;
import entity.TimeInterval;

/**
 * Supabase implementation of PriceRepository.
 * Uses REST API to interact with the price_points table.
 * <p>
 * Note: Price data is typically read-only for regular users.
 * Price updates are usually done via service role (background jobs/Edge Functions).
 */
public class SupabasePriceRepository implements PriceRepository {
    private final SupabaseClient client;

    public SupabasePriceRepository(SupabaseClient client) {
        this.client = client;
    }

    @Override
    public void savePricePoint(PricePoint pricePoint) {
        try {
            // Note: This may fail if user doesn't have INSERT permission
            // Typically only service role should insert price data
            client.insert(
                "price_points",
                pricePoint,
                PricePoint[].class
            );

        } catch (IOException e) {
            if (e.getMessage().contains("permission") || e.getMessage().contains("denied")) {
                throw new PermissionDeniedException("INSERT", "price_points");
            }
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while saving price point", e);
            }
            throw new RepositoryException("Error saving price point", e);
        }
    }

    @Override
    public void savePricePoints(List<PricePoint> pricePoints) {
        // TODO: Optimize with bulk insert instead of one-by-one
        for (PricePoint pricePoint : pricePoints) {
            savePricePoint(pricePoint);
        }
    }

    @Override
    public Optional<PricePoint> getLatestPrice(String ticker, TimeInterval interval) {
        try {
            String filter = String.format(
                "company_symbol=eq.%s&interval=eq.%s&order=timestamp.desc&limit=1",
                ticker,
                interval.name().toLowerCase()
            );

            PricePoint[] prices = client.queryWithFilter(
                "price_points",
                filter,
                PricePoint[].class
            );

            if (prices != null && prices.length > 0) {
                return Optional.of(prices[0]);
            }
            return Optional.empty();

        } catch (IOException e) {
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while fetching latest price", e);
            }
            throw new RepositoryException("Error fetching latest price for ticker: " + ticker, e);
        }
    }

    @Override
    public Map<String, PricePoint> getLatestPrices(List<String> tickers) {
        Map<String, PricePoint> result = new HashMap<>();

        // TODO: Optimize with single query using IN clause
        for (String ticker : tickers) {
            Optional<PricePoint> price = getLatestPrice(ticker, TimeInterval.DAILY);
            price.ifPresent(pricePoint -> result.put(ticker, pricePoint));
        }

        return result;
    }

    @Override
    public List<PricePoint> getHistoricalPrices(String ticker, LocalDateTime start, LocalDateTime end, TimeInterval interval) {
        try {
            String filter = String.format(
                "company_symbol=eq.%s&interval=eq.%s&timestamp=gte.%s&timestamp=lte.%s&order=timestamp.asc",
                ticker,
                interval.name().toLowerCase(),
                start.toString(),
                end.toString()
            );

            PricePoint[] prices = client.queryWithFilter(
                "price_points",
                filter,
                PricePoint[].class
            );

            return prices != null ? Arrays.asList(prices) : Collections.emptyList();

        } catch (IOException e) {
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while fetching historical prices", e);
            }
            throw new RepositoryException("Error fetching historical prices for ticker: " + ticker, e);
        }
    }

    @Override
    public void cleanup(LocalDateTime olderThan) {
        try {
            client.delete(
                "price_points",
                "timestamp=lt." + olderThan.toString()
            );

        } catch (IOException e) {
            if (e.getMessage().contains("permission") || e.getMessage().contains("denied")) {
                throw new PermissionDeniedException("DELETE", "price_points");
            }
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while cleaning up prices", e);
            }
            throw new RepositoryException("Error cleaning up old prices", e);
        }
    }

}
