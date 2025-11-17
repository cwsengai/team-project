package data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import entity.Portfolio;

/**
 * PostgreSQL/Supabase implementation of PortfolioRepository using JDBC.
 * Manages portfolio data with RLS (Row Level Security) for user isolation.
 */
public class PostgresPortfolioRepository implements PortfolioRepository {
    
    private final PostgresClient client;
    
    public PostgresPortfolioRepository() {
        this.client = new PostgresClient();
    }
    
    @Override
    public Optional<Portfolio> findById(String id) {
        String sql = "SELECT * FROM public.portfolios WHERE id = ?::uuid";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPortfolio(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding portfolio by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    @Override
    public List<Portfolio> findByUserId(String userId) {
        List<Portfolio> portfolios = new ArrayList<>();
        
        String sql = "SELECT * FROM public.portfolios WHERE user_id = ?::uuid ORDER BY created_at DESC";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    portfolios.add(mapResultSetToPortfolio(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding portfolios by user ID: " + e.getMessage());
        }
        
        return portfolios;
    }
    
    @Override
    public Portfolio save(Portfolio portfolio) {
        String id = portfolio.getId() != null ? portfolio.getId() : UUID.randomUUID().toString();
        
        // Check if portfolio exists
        Optional<Portfolio> existing = findById(id);
        
        if (existing.isPresent()) {
            return update(portfolio);
        } else {
            return insert(portfolio, id);
        }
    }
    
    private Portfolio insert(Portfolio portfolio, String id) {
        String sql = "INSERT INTO public.portfolios " +
                     "(id, user_id, name, is_simulation, initial_cash, current_cash, currency, created_at, updated_at) " +
                     "VALUES (?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            stmt.setString(1, id);
            stmt.setString(2, portfolio.getUserId());
            stmt.setString(3, portfolio.getName());
            stmt.setBoolean(4, portfolio.isSimulation());
            stmt.setDouble(5, portfolio.getInitialCash());
            stmt.setDouble(6, portfolio.getCurrentCash());
            stmt.setString(7, portfolio.getCurrency());
            stmt.setTimestamp(8, Timestamp.valueOf(portfolio.getCreatedAt() != null ? portfolio.getCreatedAt() : now));
            stmt.setTimestamp(9, Timestamp.valueOf(now));
            
            stmt.executeUpdate();
            
            // Return new portfolio with generated ID
            return new Portfolio(
                id,
                portfolio.getUserId(),
                portfolio.getName(),
                portfolio.isSimulation(),
                portfolio.getInitialCash(),
                portfolio.getCurrentCash(),
                portfolio.getCurrency(),
                portfolio.getCreatedAt() != null ? portfolio.getCreatedAt() : now,
                now
            );
        } catch (Exception e) {
            System.err.println("Error inserting portfolio: " + e.getMessage());
            throw new RuntimeException("Failed to insert portfolio", e);
        }
    }
    
    private Portfolio update(Portfolio portfolio) {
        String sql = "UPDATE public.portfolios SET " +
                     "name = ?, is_simulation = ?, current_cash = ?, currency = ?, updated_at = ? " +
                     "WHERE id = ?::uuid";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            stmt.setString(1, portfolio.getName());
            stmt.setBoolean(2, portfolio.isSimulation());
            stmt.setDouble(3, portfolio.getCurrentCash());
            stmt.setString(4, portfolio.getCurrency());
            stmt.setTimestamp(5, Timestamp.valueOf(now));
            stmt.setString(6, portfolio.getId());
            
            stmt.executeUpdate();
            
            // Return updated portfolio
            return new Portfolio(
                portfolio.getId(),
                portfolio.getUserId(),
                portfolio.getName(),
                portfolio.isSimulation(),
                portfolio.getInitialCash(),
                portfolio.getCurrentCash(),
                portfolio.getCurrency(),
                portfolio.getCreatedAt(),
                now
            );
        } catch (Exception e) {
            System.err.println("Error updating portfolio: " + e.getMessage());
            throw new RuntimeException("Failed to update portfolio", e);
        }
    }
    
    @Override
    public void updateCash(String id, double cash) {
        String sql = "UPDATE public.portfolios SET current_cash = ?, updated_at = ? WHERE id = ?::uuid";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, cash);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(3, id);
            
            int updated = stmt.executeUpdate();
            if (updated == 0) {
                System.err.println("Warning: No portfolio found with ID " + id);
            }
        } catch (Exception e) {
            System.err.println("Error updating cash: " + e.getMessage());
            throw new RuntimeException("Failed to update cash", e);
        }
    }
    
    @Override
    public void delete(String id) {
        // Note: Positions and trades will be cascade deleted due to foreign key constraints
        String sql = "DELETE FROM public.portfolios WHERE id = ?::uuid";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            int deleted = stmt.executeUpdate();
            if (deleted == 0) {
                System.err.println("Warning: No portfolio found with ID " + id);
            } else {
                System.out.println("Deleted portfolio " + id + " and all associated positions/trades");
            }
        } catch (Exception e) {
            System.err.println("Error deleting portfolio: " + e.getMessage());
            throw new RuntimeException("Failed to delete portfolio", e);
        }
    }
    
    private Portfolio mapResultSetToPortfolio(ResultSet rs) throws Exception {
        return new Portfolio(
            rs.getString("id"),
            rs.getString("user_id"),
            rs.getString("name"),
            rs.getBoolean("is_simulation"),
            rs.getDouble("initial_cash"),
            rs.getDouble("current_cash"),
            rs.getString("currency"),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
