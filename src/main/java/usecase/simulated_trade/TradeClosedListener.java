package usecase.simulated_trade;

import entity.SimulatedTradeRecord;

public interface TradeClosedListener {

    void onTradeClosed(SimulatedTradeRecord record);
}