package data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import entity.Position;

/**
 * PostgreSQL implementation of PositionRepository.
 * Manages positions (stock holdings) within portfolios.
 */
public class PostgresPositionRepository implements PositionRepository {
    private final PostgresClient client;

    public PostgresPositionRepository() {
        this.client = new PostgresClient();
    }

    private Connection getConnection() throws SQLException {
        return client.getConnection();
    }

    @Override
    public List<Position> findByPortfolioId(String portfolioId) {
        String sql = "SELECT pp.id, pp.portfolio_id, pp.company_id, c.ticker, " +
                     "pp.quantity, pp.avg_price, pp.realized_pl, pp.unrealized_pl, " +
                     "pp.last_updated " +
                     "FROM portfolio_positions pp " +
                     "JOIN companies c ON pp.company_id = c.id " +
                     "WHERE pp.portfolio_id = ?::uuid " +
                     "ORDER BY c.ticker ASC";

        List<Position> positions = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, portfolioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                positions.add(mapResultSetToPosition(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding positions by portfolio: " + e.getMessage(), e);
        }
        return positions;
    }

    @Override
    public Optional<Position> findByPortfolioAndTicker(String portfolioId, String ticker) {
        String sql = "SELECT pp.id, pp.portfolio_id, pp.company_id, c.ticker, " +
                     "pp.quantity, pp.avg_price, pp.realized_pl, pp.unrealized_pl, " +
                     "pp.last_updated " +
                     "FROM portfolio_positions pp " +
                     "JOIN companies c ON pp.company_id = c.id " +
                     "WHERE pp.portfolio_id = ?::uuid AND c.ticker = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, portfolioId);
            stmt.setString(2, ticker);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToPosition(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding position by ticker: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Position> findByPortfolioAndCompany(String portfolioId, String companyId) {
        String sql = "SELECT pp.id, pp.portfolio_id, pp.company_id, c.ticker, " +
                     "pp.quantity, pp.avg_price, pp.realized_pl, pp.unrealized_pl, " +
                     "pp.last_updated " +
                     "FROM portfolio_positions pp " +
                     "JOIN companies c ON pp.company_id = c.id " +
                     "WHERE pp.portfolio_id = ?::uuid AND pp.company_id = ?::uuid";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, portfolioId);
            stmt.setString(2, companyId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToPosition(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding position by company: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Position save(Position position) {
        if (position.getId() == null) {
            return insert(position);
        } else {
            return update(position);
        }
    }

    private Position insert(Position position) {
        String sql = "INSERT INTO portfolio_positions " +
                     "(portfolio_id, company_id, quantity, avg_price, realized_pl, unrealized_pl) " +
                     "VALUES (?::uuid, ?::uuid, ?, ?, ?, ?) " +
                     "RETURNING id, portfolio_id, company_id, quantity, avg_price, " +
                     "realized_pl, unrealized_pl, last_updated";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, position.getPortfolioId());
            stmt.setString(2, position.getCompanyId());
            stmt.setInt(3, position.getQuantity());
            stmt.setDouble(4, position.getAverageCost());
            stmt.setDouble(5, position.getRealizedPL());
            stmt.setDouble(6, position.getUnrealizedPL());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Get ticker from companies table
                String ticker = getTickerForCompany(conn, position.getCompanyId());
                return new Position(
                    rs.getString("id"),
                    rs.getString("portfolio_id"),
                    rs.getString("company_id"),
                    ticker,
                    rs.getInt("quantity"),
                    rs.getDouble("avg_price"),
                    rs.getDouble("realized_pl"),
                    rs.getDouble("unrealized_pl"),
                    rs.getTimestamp("last_updated").toLocalDateTime()
                );
            }
            throw new RuntimeException("Failed to insert position");
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting position: " + e.getMessage(), e);
        }
    }

    private Position update(Position position) {
        String sql = "UPDATE portfolio_positions " +
                     "SET quantity = ?, avg_price = ?, realized_pl = ?, unrealized_pl = ? " +
                     "WHERE id = ?::uuid " +
                     "RETURNING id, portfolio_id, company_id, quantity, avg_price, " +
                     "realized_pl, unrealized_pl, last_updated";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, position.getQuantity());
            stmt.setDouble(2, position.getAverageCost());
            stmt.setDouble(3, position.getRealizedPL());
            stmt.setDouble(4, position.getUnrealizedPL());
            stmt.setString(5, position.getId());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Get ticker from companies table
                String ticker = getTickerForCompany(conn, rs.getString("company_id"));
                return new Position(
                    rs.getString("id"),
                    rs.getString("portfolio_id"),
                    rs.getString("company_id"),
                    ticker,
                    rs.getInt("quantity"),
                    rs.getDouble("avg_price"),
                    rs.getDouble("realized_pl"),
                    rs.getDouble("unrealized_pl"),
                    rs.getTimestamp("last_updated").toLocalDateTime()
                );
            }
            throw new RuntimeException("Failed to update position");
        } catch (SQLException e) {
            throw new RuntimeException("Error updating position: " + e.getMessage(), e);
        }
    }

    @Override
    public void updatePL(String positionId, double realizedPL, double unrealizedPL) {
        String sql = "UPDATE portfolio_positions " +
                     "SET realized_pl = ?, unrealized_pl = ? " +
                     "WHERE id = ?::uuid";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, realizedPL);
            stmt.setDouble(2, unrealizedPL);
            stmt.setString(3, positionId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Position not found: " + positionId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating P/L: " + e.getMessage(), e);
        }
    }

    private Position mapResultSetToPosition(ResultSet rs) throws SQLException {
        return new Position(
            rs.getString("id"),
            rs.getString("portfolio_id"),
            rs.getString("company_id"),
            rs.getString("ticker"),
            rs.getInt("quantity"),
            rs.getDouble("avg_price"),
            rs.getDouble("realized_pl"),
            rs.getDouble("unrealized_pl"),
            rs.getTimestamp("last_updated").toLocalDateTime()
        );
    }

    private String getTickerForCompany(Connection conn, String companyId) throws SQLException {
        String sql = "SELECT ticker FROM companies WHERE id = ?::uuid";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, companyId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("ticker");
            }
            throw new RuntimeException("Company not found: " + companyId);
        }
    }
}
