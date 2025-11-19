package data_access.repository.supabase;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import data_access.client.SupabaseClient;
import data_access.exception.DatabaseConnectionException;
import data_access.exception.EntityNotFoundException;
import data_access.exception.PermissionDeniedException;
import data_access.exception.RepositoryException;
import data_access.repository.PortfolioRepository;
import entity.Portfolio;

/**
 * Supabase implementation of PortfolioRepository.
 * Uses REST API to interact with the portfolios table.
 * Row Level Security (RLS) automatically filters by authenticated user.
 */
public class SupabasePortfolioRepository implements PortfolioRepository {
    private final SupabaseClient client;

    public SupabasePortfolioRepository(SupabaseClient client) {
        this.client = client;
    }

    @Override
    public Optional<Portfolio> findById(String id) {
        try {
            Portfolio[] portfolios = client.queryWithFilter(
                "portfolios",
                "id=eq." + id,
                Portfolio[].class
            );

            if (portfolios != null && portfolios.length > 0) {
                return Optional.of(portfolios[0]);
            }
            return Optional.empty();

        } catch (IOException e) {
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while fetching portfolio", e);
            }
            throw new RepositoryException("Error fetching portfolio: " + id, e);
        }
    }

    @Override
    public List<Portfolio> findByUserId(String userId) {
        try {
            Portfolio[] portfolios = client.query(
                "portfolios",
                Portfolio[].class
            );

            return portfolios != null ? Arrays.asList(portfolios) : Collections.emptyList();

        } catch (IOException e) {
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while fetching portfolios", e);
            }
            throw new RepositoryException("Error fetching portfolios for user: " + userId, e);
        }
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        try {
            if (portfolio.getId() == null || portfolio.getId().isEmpty()) {
                return insert(portfolio);
            } else {
                return update(portfolio);
            }

        } catch (IOException e) {
            if (e.getMessage().contains("permission") || e.getMessage().contains("denied")) {
                throw new PermissionDeniedException("WRITE", "portfolios");
            }
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while saving portfolio", e);
            }
            throw new RepositoryException("Error saving portfolio", e);
        }
    }

    private Portfolio insert(Portfolio portfolio) throws IOException {
        Portfolio[] result = client.insert(
            "portfolios",
            portfolio,
            Portfolio[].class
        );

        if (result != null && result.length > 0) {
            return result[0];
        }
        throw new RepositoryException("Insert failed: no data returned from database");
    }

    private Portfolio update(Portfolio portfolio) throws IOException {
        Portfolio[] result = client.update(
            "portfolios",
            "id=eq." + portfolio.getId(),
            portfolio,
            Portfolio[].class
        );

        if (result != null && result.length > 0) {
            return result[0];
        }
        throw new EntityNotFoundException("Portfolio", portfolio.getId());
    }

    @Override
    public void updateCash(String id, double cash) {
        try {
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("current_cash", cash);

            Portfolio[] result = client.update(
                "portfolios",
                "id=eq." + id,
                updateData,
                Portfolio[].class
            );
            
            if (result == null || result.length == 0) {
                throw new EntityNotFoundException("Portfolio", id);
            }

        } catch (IOException e) {
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while updating cash", e);
            }
            throw new RepositoryException("Error updating cash for portfolio: " + id, e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            client.delete("portfolios", "id=eq." + id);

        } catch (IOException e) {
            if (e.getMessage().contains("permission") || e.getMessage().contains("denied")) {
                throw new PermissionDeniedException("DELETE", "portfolios");
            }
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while deleting portfolio", e);
            }
            throw new RepositoryException("Error deleting portfolio: " + id, e);
        }
    }
}
