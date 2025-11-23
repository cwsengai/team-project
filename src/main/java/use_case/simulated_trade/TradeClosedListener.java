package use_case.simulated_trade;

import entity.SimulatedTradeRecord;

//Interface for components that need to be notified when a trade is finalized (closed).

public interface TradeClosedListener {
    void onTradeClosed(SimulatedTradeRecord record);
}