package interface_adapter.simulated_trading;

import use_case.simulated_trade.SimulatedTradeInputBoundary;
import use_case.simulated_trade.SimulatedTradeInputData;
import use_case.update_market.UpdateMarketInputBoundary;

public class TradingController {

    private final UpdateMarketInputBoundary updateMarketInteractor;
    private final SimulatedTradeInputBoundary tradeInteractor;

    public TradingController(UpdateMarketInputBoundary updateMarketInteractor,
                             SimulatedTradeInputBoundary tradeInteractor) {
        this.updateMarketInteractor = updateMarketInteractor;
        this.tradeInteractor = tradeInteractor;
    }

    //Triggered by the View's Timer (e.g., every second).

    public void executeTimerTick() {
        updateMarketInteractor.executeExecuteTick();
    }

    //Triggered by Buy/Sell buttons;.
    public void executeTrade(String ticker, double amount, boolean isBuy, double currentPrice) {
        SimulatedTradeInputData inputData = new SimulatedTradeInputData(
                ticker,
                isBuy,
                amount,
                currentPrice
        );
        tradeInteractor.executeTrade(inputData);
    }
}