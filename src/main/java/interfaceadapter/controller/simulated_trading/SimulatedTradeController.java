package interfaceadapter.controller.simulated_trading;

import usecase.simulated_trade.SimulatedTradeInputData;
import usecase.simulated_trade.SimulatedTradeInteractor;

public class SimulatedTradeController {

    private final SimulatedTradeInteractor interactor;

    public SimulatedTradeController(SimulatedTradeInteractor interactor) {
        this.interactor = interactor;
    }

    /**
     * Saves a trade for the current user.
     *
     * @param ticker the stock ticker symbol
     * @param isBuy true if this is a buy order, false if sell
     * @param amount the trade amount
     * @param currentPrice the current price at the time of trade
     */
    public void saveTrade(String ticker, boolean isBuy, double amount, double currentPrice) {
        // This method now matches the Clean Architecture input style
        SimulatedTradeInputData input = new SimulatedTradeInputData(
            ticker,
            isBuy,
            amount,
            currentPrice
        );
        interactor.executeTrade(input);
    }
}
