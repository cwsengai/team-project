package use_case.simulated_trade;

import java.time.LocalDateTime;
import java.util.UUID;

import entity.SimulatedTradeRecord;

import use_case.session.SessionDataAccessInterface;

public class SimulatedTradeInteractor {
    private final SimulatedTradeDataAccessInterface dataAccess;
    private final SessionDataAccessInterface sessionDataAccess;

    public SimulatedTradeInteractor(SimulatedTradeDataAccessInterface dataAccess, SessionDataAccessInterface sessionDataAccess) {
        this.dataAccess = dataAccess;
        this.sessionDataAccess = sessionDataAccess;
    }

    public void execute(String ticker, boolean isLong, int quantity, double entryPrice, double exitPrice, LocalDateTime entryTime, LocalDateTime exitTime) {
        // 1. Business Logic / Validation
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");

        double pnl = (exitPrice - entryPrice) * quantity;
        if (!isLong) pnl = -pnl; // Invert logic for shorts

        // 2. Create Entity
        SimulatedTradeRecord trade = new SimulatedTradeRecord(
            ticker, isLong, quantity, entryPrice, exitPrice, pnl, entryTime, exitTime
        );

        // Get current user ID from session manager
        UUID currentUserId = sessionDataAccess.getCurrentUserId();
        dataAccess.saveTrade(trade, currentUserId);
    }
}