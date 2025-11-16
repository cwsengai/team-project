package data_access;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import entity.Position;

/**
 * Supabase implementation of PositionRepository.
 * Uses REST API to interact with the portfolio_positions table.
 * Row Level Security (RLS) automatically filters by authenticated user.
 */
public class SupabasePositionRepository implements PositionRepository {
    private final SupabaseClient client;

    /**
     * Creates a new Supabase position repository.
     *
     * @param client the authenticated Supabase client
     */
    public SupabasePositionRepository(SupabaseClient client) {
        this.client = client;
    }

    @Override
    public List<Position> findByPortfolioId(String portfolioId) {
        try {
            // Query: GET /rest/v1/portfolio_positions?portfolio_id=eq.{portfolioId}
            // RLS ensures user can only see positions in their own portfolios
            Position[] positions = client.queryWithFilter(
                "portfolio_positions",
                "portfolio_id=eq." + portfolioId,
                Position[].class
            );

            return positions != null ? Arrays.asList(positions) : Collections.emptyList();

        } catch (IOException e) {
            throw new RuntimeException("Error fetching positions: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Position> findByPortfolioAndTicker(String portfolioId, String ticker) {
        try {
            // First, get company_id from ticker
            Company[] companies = client.queryWithFilter(
                "companies",
                "ticker=eq." + ticker,
                Company[].class
            );

            if (companies == null || companies.length == 0) {
                return Optional.empty();
            }

            String companyId = companies[0].getId();

            // Query by portfolio_id and company_id
            return findByPortfolioAndCompany(portfolioId, companyId);

        } catch (IOException e) {
            throw new RuntimeException("Error fetching position by ticker: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Position> findByPortfolioAndCompany(String portfolioId, String companyId) {
        try {
            // Query: GET /rest/v1/portfolio_positions?portfolio_id=eq.{portfolioId}&company_id=eq.{companyId}
            String filter = String.format(
                "portfolio_id=eq.%s&company_id=eq.%s",
                portfolioId,
                companyId
            );

            Position[] positions = client.queryWithFilter(
                "portfolio_positions",
                filter,
                Position[].class
            );

            if (positions != null && positions.length > 0) {
                return Optional.of(positions[0]);
            }
            return Optional.empty();

        } catch (IOException e) {
            throw new RuntimeException("Error fetching position: " + e.getMessage(), e);
        }
    }

    @Override
    public Position save(Position position) {
        try {
            // If position has no ID, it's a new insert
            // Otherwise, check if it exists and update
            if (position.getId() == null || position.getId().isEmpty()) {
                return insert(position);
            } else {
                // Try to find existing position
                Optional<Position> existing = findByPortfolioAndCompany(
                    position.getPortfolioId(),
                    position.getCompanyId()
                );

                if (existing.isPresent()) {
                    return update(position);
                } else {
                    return insert(position);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error saving position: " + e.getMessage(), e);
        }
    }

    private Position insert(Position position) throws IOException {
        // Insert new position
        Position[] result = client.insert(
            "portfolio_positions",
            position,
            Position[].class
        );

        if (result != null && result.length > 0) {
            return result[0];
        }
        throw new RuntimeException("Insert failed: no data returned");
    }

    private Position update(Position position) throws IOException {
        // Update existing position
        Position[] result = client.update(
            "portfolio_positions",
            "id=eq." + position.getId(),
            position,
            Position[].class
        );

        if (result != null && result.length > 0) {
            return result[0];
        }
        throw new RuntimeException("Update failed: position not found");
    }

    @Override
    public void updatePL(String positionId, double realizedPL, double unrealizedPL) {
        try {
            // Create a partial update object
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("realized_pl", realizedPL);
            updateData.put("unrealized_pl", unrealizedPL);
            updateData.put("last_updated", java.time.LocalDateTime.now().toString());

            client.update(
                "portfolio_positions",
                "id=eq." + positionId,
                updateData,
                Position[].class
            );

        } catch (IOException e) {
            throw new RuntimeException("Error updating P/L: " + e.getMessage(), e);
        }
    }

    // Helper class for Company (minimal version for this repository)
    private static class Company {
        private String id;
        private String ticker;

        public String getId() {
            return id;
        }
    }
}
