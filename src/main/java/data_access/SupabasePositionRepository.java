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
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while fetching positions", e);
            }
            throw new RepositoryException("Error fetching positions for portfolio: " + portfolioId, e);
        }
    }

    @Override
    public Optional<Position> findByPortfolioAndTicker(String portfolioId, String ticker) {
        try {
            // Query by portfolio_id and instrument_symbol (ticker)
            // The schema uses instrument_symbol which references financial_instruments.symbol
            String filter = String.format(
                "portfolio_id=eq.%s&instrument_symbol=eq.%s",
                portfolioId,
                ticker
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
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while fetching position", e);
            }
            throw new RepositoryException("Error fetching position by ticker: " + ticker, e);
        }
    }

    @Override
    public Optional<Position> findByPortfolioAndCompany(String portfolioId, String companySymbol) {
        try {
            // Use instrument_symbol instead of company_id
            // companySymbol parameter is actually the instrument symbol
            return findByPortfolioAndTicker(portfolioId, companySymbol);

        } catch (Exception e) {
            throw new RepositoryException("Error fetching position for company: " + companySymbol, e);
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
                Optional<Position> existing = findByPortfolioAndTicker(
                    position.getPortfolioId(),
                    position.getInstrumentSymbol()
                );

                if (existing.isPresent()) {
                    return update(position);
                } else {
                    return insert(position);
                }
            }

        } catch (IOException e) {
            if (e.getMessage().contains("permission") || e.getMessage().contains("denied")) {
                throw new PermissionDeniedException("WRITE", "portfolio_positions");
            }
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while saving position", e);
            }
            throw new RepositoryException("Error saving position", e);
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
        throw new RepositoryException("Insert failed: no data returned from database");
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
        throw new EntityNotFoundException("Position", position.getId());
    }

    @Override
    public void updatePL(String positionId, double realizedPL, double unrealizedPL) {
        try {
            // Create a partial update object
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("realized_pl", realizedPL);
            updateData.put("unrealized_pl", unrealizedPL);
            updateData.put("last_updated", java.time.LocalDateTime.now().toString());

            Position[] result = client.update(
                "portfolio_positions",
                "id=eq." + positionId,
                updateData,
                Position[].class
            );
            
            if (result == null || result.length == 0) {
                throw new EntityNotFoundException("Position", positionId);
            }

        } catch (IOException e) {
            if (e.getMessage().contains("Failed to connect") || e.getMessage().contains("timeout")) {
                throw new DatabaseConnectionException("Failed to connect to database while updating P/L", e);
            }
            throw new RepositoryException("Error updating P/L for position: " + positionId, e);
        }
    }

}
