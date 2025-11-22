package data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import entity.PricePoint;
import entity.TimeInterval;

/**
 * PostgreSQL/Supabase implementation of PriceRepository using JDBC.
 * Manages historical price data for stocks.
 */
public class PostgresPriceRepository implements PriceRepository {
    
    private final PostgresClient client;
    private final PostgresCompanyRepository companyRepo;
    
    public PostgresPriceRepository() {
        this.client = new PostgresClient();
        this.companyRepo = new PostgresCompanyRepository();
    }
    
    @Override
    public void savePricePoint(PricePoint pricePoint) {
        String sql = "INSERT INTO public.price_points " +
                     "(id, company_id, timestamp, interval, open, high, low, close, volume, source) " +
                     "VALUES (?::uuid, (SELECT id FROM public.companies WHERE ticker = ?), ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT (company_id, interval, timestamp) DO UPDATE SET " +
                     "open = EXCLUDED.open, high = EXCLUDED.high, low = EXCLUDED.low, " +
                     "close = EXCLUDED.close, volume = EXCLUDED.volume, source = EXCLUDED.source";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String id = pricePoint.getId() != null ? pricePoint.getId() : UUID.randomUUID().toString();
            
            stmt.setString(1, id);
            stmt.setString(2, pricePoint.getCompanyId()); // ticker will be looked up to get UUID
            stmt.setTimestamp(3, Timestamp.valueOf(pricePoint.getTimestamp()));
            stmt.setString(4, pricePoint.getInterval().name());
            setDoubleOrNull(stmt, 5, pricePoint.getOpen());
            setDoubleOrNull(stmt, 6, pricePoint.getHigh());
            setDoubleOrNull(stmt, 7, pricePoint.getLow());
            setDoubleOrNull(stmt, 8, pricePoint.getClose());
            setDoubleOrNull(stmt, 9, pricePoint.getVolume());
            stmt.setString(10, pricePoint.getSource());
            
            stmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error saving price point: " + e.getMessage());
            throw new RuntimeException("Failed to save price point", e);
        }
    }
    
    @Override
    public void savePricePoints(List<PricePoint> pricePoints) {
        String sql = "INSERT INTO public.price_points " +
                     "(id, company_id, timestamp, interval, open, high, low, close, volume, source) " +
                     "VALUES (?::uuid, (SELECT id FROM public.companies WHERE ticker = ?), ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT (company_id, interval, timestamp) DO UPDATE SET " +
                     "open = EXCLUDED.open, high = EXCLUDED.high, low = EXCLUDED.low, " +
                     "close = EXCLUDED.close, volume = EXCLUDED.volume, source = EXCLUDED.source";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (PricePoint pricePoint : pricePoints) {
                String id = pricePoint.getId() != null ? pricePoint.getId() : UUID.randomUUID().toString();
                
                stmt.setString(1, id);
                stmt.setString(2, pricePoint.getCompanyId()); // ticker will be looked up to get UUID
                stmt.setTimestamp(3, Timestamp.valueOf(pricePoint.getTimestamp()));
                stmt.setString(4, pricePoint.getInterval().name());
                setDoubleOrNull(stmt, 5, pricePoint.getOpen());
                setDoubleOrNull(stmt, 6, pricePoint.getHigh());
                setDoubleOrNull(stmt, 7, pricePoint.getLow());
                setDoubleOrNull(stmt, 8, pricePoint.getClose());
                setDoubleOrNull(stmt, 9, pricePoint.getVolume());
                stmt.setString(10, pricePoint.getSource());
                
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        } catch (Exception e) {
            System.err.println("Error saving price points: " + e.getMessage());
            throw new RuntimeException("Failed to save price points", e);
        }
    }
    
    @Override
    public Optional<PricePoint> getLatestPrice(String ticker, TimeInterval interval) {
        String sql = "SELECT pp.* FROM public.price_points pp " +
                     "JOIN public.companies c ON pp.company_id = c.id " +
                     "WHERE c.ticker = ? AND pp.interval = ? " +
                     "ORDER BY pp.timestamp DESC LIMIT 1";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ticker);
            stmt.setString(2, interval.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPricePoint(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting latest price: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    @Override
    public Map<String, PricePoint> getLatestPrices(List<String> tickers) {
        Map<String, PricePoint> result = new HashMap<>();
        
        if (tickers.isEmpty()) {
            return result;
        }
        
        // Build IN clause with placeholders
        String placeholders = String.join(",", tickers.stream().map(t -> "?").toArray(String[]::new));
        
        String sql = "SELECT DISTINCT ON (c.ticker) c.ticker, pp.* " +
                     "FROM public.price_points pp " +
                     "JOIN public.companies c ON pp.company_id = c.id " +
                     "WHERE c.ticker IN (" + placeholders + ") " +
                     "ORDER BY c.ticker, pp.timestamp DESC";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < tickers.size(); i++) {
                stmt.setString(i + 1, tickers.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String ticker = rs.getString("ticker");
                    PricePoint pricePoint = mapResultSetToPricePoint(rs);
                    result.put(ticker, pricePoint);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting latest prices: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public List<PricePoint> getHistoricalPrices(String ticker, LocalDateTime start, 
                                                  LocalDateTime end, TimeInterval interval) {
        List<PricePoint> pricePoints = new ArrayList<>();
        
        String sql = "SELECT pp.* FROM public.price_points pp " +
                     "JOIN public.companies c ON pp.company_id = c.id " +
                     "WHERE c.ticker = ? AND pp.interval = ? " +
                     "AND pp.timestamp >= ? AND pp.timestamp <= ? " +
                     "ORDER BY pp.timestamp ASC";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ticker);
            stmt.setString(2, interval.name());
            stmt.setTimestamp(3, Timestamp.valueOf(start));
            stmt.setTimestamp(4, Timestamp.valueOf(end));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pricePoints.add(mapResultSetToPricePoint(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting historical prices: " + e.getMessage());
        }
        
        return pricePoints;
    }
    
    @Override
    public void cleanup(LocalDateTime olderThan) {
        String sql = "DELETE FROM public.price_points WHERE timestamp < ?";
        
        try (Connection conn = client.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(olderThan));
            int deleted = stmt.executeUpdate();
            
            System.out.println("Cleaned up " + deleted + " old price points");
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
            throw new RuntimeException("Failed to cleanup price points", e);
        }
    }
    
    private PricePoint mapResultSetToPricePoint(ResultSet rs) throws Exception {
        return new PricePoint(
            rs.getString("id"),
            rs.getString("company_id"),
            rs.getTimestamp("timestamp").toLocalDateTime(),
            TimeInterval.valueOf(rs.getString("interval")),
            getDoubleOrNull(rs, "open"),
            getDoubleOrNull(rs, "high"),
            getDoubleOrNull(rs, "low"),
            getDoubleOrNull(rs, "close"),
            getDoubleOrNull(rs, "volume"),
            rs.getString("source")
        );
    }
    
    private void setDoubleOrNull(PreparedStatement stmt, int index, Double value) throws Exception {
        if (value == null) {
            stmt.setNull(index, java.sql.Types.DOUBLE);
        } else {
            stmt.setDouble(index, value);
        }
    }
    
    private Double getDoubleOrNull(ResultSet rs, String columnName) throws Exception {
        double value = rs.getDouble(columnName);
        return rs.wasNull() ? null : value;
    }
}
