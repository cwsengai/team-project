package data_access.repository.supabase;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import data_access.client.SupabaseClient;
import data_access.exception.DatabaseConnectionException;
import data_access.exception.PermissionDeniedException;
import data_access.exception.RepositoryException;
import data_access.repository.TradeRepository;
import entity.Trade;

/**
 * Supabase implementation of TradeRepository.
 * Uses REST API to interact with the trades table.
 * Row Level Security (RLS) automatically filters by authenticated user.
 * 
 * Note: Trades are immutable once created (no update/delete operations).
 */
public class SupabaseTradeRepository implements TradeRepository {
    private final SupabaseClient client;

    /**
     * Creates a new Supabase trade repository.
     *
     * @param client the authenticated Supabase client
     */
    public SupabaseTradeRepository(SupabaseClient client) {
        this.client = client;
    }

    @Override
    public Trade save(Trade trade) {
        try {
            // Trades are always inserts (immutable)
            Trade[] result = client.insert(
                "trades",
                trade,
                Trade[].class
            );

            if (result != null && result.length > 0) {
                return result[0];
            }
            throw new RepositoryException("Insert failed: no data returned from database");

        } catch (IOException e) {
            if (e.getMessage().contains("permission") || e.getMessage().contains("denied")) {
                throw new PermissionDeniedException("INSERT", "trades");
            }
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while saving trade", e);
            }
            throw new RepositoryException("Error saving trade", e);
        }
    }

    @Override
    public List<Trade> findByPortfolioId(String portfolioId) {
        try {
            // Query: GET /rest/v1/trades?portfolio_id=eq.{portfolioId}&order=executed_at.desc
            // RLS ensures user can only see trades in their own portfolios
            String filter = String.format(
                "portfolio_id=eq.%s&order=executed_at.desc",
                portfolioId
            );

            Trade[] trades = client.queryWithFilter(
                "trades",
                filter,
                Trade[].class
            );

            return trades != null ? Arrays.asList(trades) : Collections.emptyList();

        } catch (IOException e) {
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while fetching trades", e);
            }
            throw new RepositoryException("Error fetching trades for portfolio: " + portfolioId, e);
        }
    }

    @Override
    public List<Trade> findByPositionId(String positionId) {
        try {
            // Query: GET /rest/v1/trades?position_id=eq.{positionId}&order=executed_at.asc
            String filter = String.format(
                "position_id=eq.%s&order=executed_at.asc",
                positionId
            );

            Trade[] trades = client.queryWithFilter(
                "trades",
                filter,
                Trade[].class
            );

            return trades != null ? Arrays.asList(trades) : Collections.emptyList();

        } catch (IOException e) {
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while fetching trades", e);
            }
            throw new RepositoryException("Error fetching trades for position: " + positionId, e);
        }
    }

    @Override
    public List<Trade> findByPortfolioInDateRange(String portfolioId, LocalDateTime start, LocalDateTime end) {
        try {
            // Query: GET /rest/v1/trades?portfolio_id=eq.{portfolioId}&executed_at=gte.{start}&executed_at=lte.{end}&order=executed_at.desc
            String filter = String.format(
                "portfolio_id=eq.%s&executed_at=gte.%s&executed_at=lte.%s&order=executed_at.desc",
                portfolioId,
                start.toString(),
                end.toString()
            );

            Trade[] trades = client.queryWithFilter(
                "trades",
                filter,
                Trade[].class
            );

            return trades != null ? Arrays.asList(trades) : Collections.emptyList();

        } catch (IOException e) {
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while fetching trades", e);
            }
            throw new RepositoryException("Error fetching trades in date range for portfolio: " + portfolioId, e);
        }
    }
}
