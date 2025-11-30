package usecase.simulated_trade;
import java.util.UUID;

import entity.SimulatedTradeRecord;

public interface SimulatedTradeDataAccessInterface {
    void saveTrade(SimulatedTradeRecord trade, UUID userId);
}