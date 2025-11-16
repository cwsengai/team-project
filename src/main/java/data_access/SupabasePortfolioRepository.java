package data_access;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import entity.Portfolio;

/**
 * Supabase implementation of PortfolioRepository.
 * Uses REST API to interact with the portfolios table.
 * Row Level Security (RLS) automatically filters by authenticated user.
 */
public class SupabasePortfolioRepository implements PortfolioRepository {
    private final SupabaseClient client;

    /**
     * Creates a new Supabase portfolio repository.
     *
     * @param client the authenticated Supabase client
     */
    public SupabasePortfolioRepository(SupabaseClient client) {
        this.client = client;
    }

    @Override
    public Optional<Portfolio> findById(String id) {
        try {
            // Query: GET /rest/v1/portfolios?id=eq.{id}
            // RLS ensures user can only see their own portfolios
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
            throw new RuntimeException("Error fetching portfolio: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Portfolio> findByUserId(String userId) {
        try {
            // RLS automatically filters by auth.uid()
            // So we can just query all portfolios - only user's will be returned
            Portfolio[] portfolios = client.query(
                "portfolios",
                Portfolio[].class
            );

            return portfolios != null ? Arrays.asList(portfolios) : Collections.emptyList();

        } catch (IOException e) {
            throw new RuntimeException("Error fetching portfolios: " + e.getMessage(), e);
        }
    }

    @Override
    public Portfolio save(Portfolio portfolio) {
        try {
            // If portfolio has no ID, it's a new insert (Supabase will generate UUID)
            // Otherwise, it's an update
            if (portfolio.getId() == null || portfolio.getId().isEmpty()) {
                return insert(portfolio);
            } else {
                return update(portfolio);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error saving portfolio: " + e.getMessage(), e);
        }
    }

    private Portfolio insert(Portfolio portfolio) throws IOException {
        // Supabase will auto-generate UUID and timestamps
        Portfolio[] result = client.insert(
            "portfolios",
            portfolio,
            Portfolio[].class
        );

        if (result != null && result.length > 0) {
            return result[0];
        }
        throw new RuntimeException("Insert failed: no data returned");
    }

    private Portfolio update(Portfolio portfolio) throws IOException {
        // Update by ID
        Portfolio[] result = client.update(
            "portfolios",
            "id=eq." + portfolio.getId(),
            portfolio,
            Portfolio[].class
        );

        if (result != null && result.length > 0) {
            return result[0];
        }
        throw new RuntimeException("Update failed: portfolio not found");
    }

    @Override
    public void updateCash(String id, double cash) {
        try {
            // Create a partial update object (only the cash field)
            String updateJson = String.format("{\"current_cash\": %f}", cash);

            client.update(
                "portfolios",
                "id=eq." + id,
                updateJson,
                Portfolio[].class
            );

        } catch (IOException e) {
            throw new RuntimeException("Error updating cash: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            // RLS will prevent deleting portfolios user doesn't own
            // CASCADE delete will remove positions and trades
            client.delete("portfolios", "id=eq." + id);

        } catch (IOException e) {
            throw new RuntimeException("Error deleting portfolio: " + e.getMessage(), e);
        }
    }
}
