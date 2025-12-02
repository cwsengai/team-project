package usecase.simulated_trade;

import java.util.UUID;

import entity.SimulatedTradeRecord;

/**
 * Data access interface for saving simulated trade records.
 */
public interface SimulatedTradeDataAccessInterface {

    /**
     * Saves a simulated trade record associated with the specified user.
     *
     * @param trade the simulated trade record to be saved
     * @param userId the unique identifier of the user who executed the trade
     */
    void saveTrade(SimulatedTradeRecord trade, UUID userId);
}
