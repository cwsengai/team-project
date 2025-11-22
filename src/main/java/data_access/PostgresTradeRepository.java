package data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entity.Trade;
import entity.TradeType;

/**
 * PostgreSQL/Supabase implementation of TradeRepository using JDBC.
 * Manages immutable trade transaction records.
 */
public class PostgresTradeRepository implements TradeRepository {
    
    private final PostgresClient client;
    
    public PostgresTradeRepository() {
        this.client = new PostgresClient();
    }
    
    @Override
    public Trade save(Trade trade) {
        String id = trade.getId() != null ? trade.getId() : UUID.randomUUID().toString();
        
        String sql = "INSERT INTO public.trades " +
                     "(id, portfolio_id, position_id, company_id, ticker, trade_type, quantity, price, fees, executed_at, created_at) " +
                     "VALUES (?::uuid, ?::uuid, ?::uuid, ?::uuid, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            LocalDateTime now = LocalDateTime.now();
            
            stmt.setString(1, id);
            stmt.setString(2, trade.getPortfolioId());
            setStringOrNull(stmt, 3, trade.getPositionId());
            stmt.setString(4, trade.getCompanyId());
            stmt.setString(5, trade.getTicker());
            stmt.setString(6, trade.getTradeType().name());
            stmt.setInt(7, trade.getQuantity());
            stmt.setDouble(8, trade.getPrice());
            stmt.setDouble(9, trade.getFees());
            stmt.setTimestamp(10, Timestamp.valueOf(trade.getExecutedAt() != null ? trade.getExecutedAt() : now));
            stmt.setTimestamp(11, Timestamp.valueOf(now));
            
            stmt.executeUpdate();
            
            // Return new trade with generated ID
            return new Trade(
                id,
                trade.getPortfolioId(),
                trade.getPositionId(),
                trade.getCompanyId(),
                trade.getTicker(),
                trade.getTradeType(),
                trade.getQuantity(),
                trade.getPrice(),
                trade.getFees(),
                trade.getExecutedAt() != null ? trade.getExecutedAt() : now,
                now
            );
        } catch (Exception e) {
            System.err.println("Error saving trade: " + e.getMessage());
            throw new RuntimeException("Failed to save trade", e);
        }
    }
    
    @Override
    public List<Trade> findByPortfolioId(String portfolioId) {
        List<Trade> trades = new ArrayList<>();
        
        String sql = "SELECT * FROM public.trades WHERE portfolio_id = ?::uuid ORDER BY executed_at DESC";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, portfolioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trades.add(mapResultSetToTrade(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding trades by portfolio ID: " + e.getMessage());
        }
        
        return trades;
    }
    
    @Override
    public List<Trade> findByPositionId(String positionId) {
        List<Trade> trades = new ArrayList<>();
        
        String sql = "SELECT * FROM public.trades WHERE position_id = ?::uuid ORDER BY executed_at ASC";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, positionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trades.add(mapResultSetToTrade(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding trades by position ID: " + e.getMessage());
        }
        
        return trades;
    }
    
    @Override
    public List<Trade> findByPortfolioInDateRange(String portfolioId, LocalDateTime start, LocalDateTime end) {
        List<Trade> trades = new ArrayList<>();
        
        String sql = "SELECT * FROM public.trades " +
                     "WHERE portfolio_id = ?::uuid AND executed_at >= ? AND executed_at <= ? " +
                     "ORDER BY executed_at ASC";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, portfolioId);
            stmt.setTimestamp(2, Timestamp.valueOf(start));
            stmt.setTimestamp(3, Timestamp.valueOf(end));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    trades.add(mapResultSetToTrade(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding trades in date range: " + e.getMessage());
        }
        
        return trades;
    }
    
    private Trade mapResultSetToTrade(ResultSet rs) throws Exception {
        return new Trade(
            rs.getString("id"),
            rs.getString("portfolio_id"),
            rs.getString("position_id"),
            rs.getString("company_id"),
            rs.getString("ticker"),
            TradeType.valueOf(rs.getString("trade_type")),
            rs.getInt("quantity"),
            rs.getDouble("price"),
            rs.getDouble("fees"),
            rs.getTimestamp("executed_at").toLocalDateTime(),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
    
    private void setStringOrNull(PreparedStatement stmt, int index, String value) throws Exception {
        if (value == null) {
            stmt.setNull(index, java.sql.Types.VARCHAR);
        } else {
            stmt.setString(index, value);
        }
    }
}
