package usecase.simulated_trade;

import entity.SimulatedTradeRecord;

/**
 * Listener interface for receiving notifications when a simulated trade
 * has been closed in the trading engine.
 */
public interface TradeClosedListener {

    /**
     * Handles a callback when a simulated trade has been closed.
     *
     * @param record the SimulatedTradeRecord containing details of the closed trade
     */
    void onTradeClosed(SimulatedTradeRecord record);
}
