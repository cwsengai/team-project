package interface_adapter.controller.simulated_trading;

import use_case.simulated_trade.SimulatedTradeInputData;
import use_case.simulated_trade.SimulatedTradeInteractor;

public class SimulatedTradeController {

    private final SimulatedTradeInteractor interactor;

    public SimulatedTradeController(SimulatedTradeInteractor interactor) {
        this.interactor = interactor;
    }

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