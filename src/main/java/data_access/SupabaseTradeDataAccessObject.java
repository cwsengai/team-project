package data_access;

import java.sql.Connection;
import java.sql.PreparedStatement; // Created in next step
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import entity.SimulatedTradeRecord;
import use_case.simulated_trade.SimulatedTradeDataAccessInterface;

public class SupabaseTradeDataAccessObject implements SimulatedTradeDataAccessInterface {

    // In a real app, inject the connection or connection pool here
    private Connection getConnection() throws SQLException {
        // RETURN YOUR DB CONNECTION HERE (e.g., DriverManager.getConnection(...))
        return null; 
    }

    @Override
    public void saveTrade(SimulatedTradeRecord trade, UUID userId) {
        String insertTrade = 
            "INSERT INTO public.trades (user_id, ticker, is_long, quantity, entry_price, exit_price, realized_pnl, entry_time, exit_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Transaction: Insert Trade -> Update Portfolio Balance
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Start Transaction

            try (PreparedStatement pst = conn.prepareStatement(insertTrade)) {
                pst.setObject(1, userId);
                pst.setString(2, trade.getTicker());
                pst.setBoolean(3, trade.isLong());
                pst.setInt(4, trade.getQuantity());
                pst.setDouble(5, trade.getEntryPrice());
                pst.setDouble(6, trade.getExitPrice());
                pst.setDouble(7, trade.getRealizedPnL());
                pst.setTimestamp(8, Timestamp.valueOf(trade.getEntryTime()));
                pst.setTimestamp(9, Timestamp.valueOf(trade.getExitTime()));
                pst.executeUpdate();
            }

            // Cash balance is updated automatically via DB trigger
            conn.commit(); // Save changes
        } catch (SQLException e) {
            throw new RuntimeException("Failed to store trade", e);
        }
    }
}