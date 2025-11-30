package interfaceadapter.simulated_trading;

import usecase.simulated_trade.SimulatedTradeInputBoundary;
import usecase.simulated_trade.SimulatedTradeInputData;
import usecase.update_market.UpdateMarketInputBoundary;

/**
 * Controller responsible for handling user actions in the trading view.
 * Delegates requests to market update and trade execution interactors.
 */
public class TradingController {

    private final UpdateMarketInputBoundary updateMarketInteractor;
    private final SimulatedTradeInputBoundary tradeInteractor;
    private final TradingPresenter tradingPresenter;

    /**
     * Constructs a TradingController.
     *
     * @param updateMarketInteractor interactor handling market update ticks
     * @param tradeInteractor interactor responsible for executing trades
     * @param tradingPresenter presenter used for returning to the previous view
     */
    public TradingController(UpdateMarketInputBoundary updateMarketInteractor,
                             SimulatedTradeInputBoundary tradeInteractor,
                             TradingPresenter tradingPresenter) {
        this.updateMarketInteractor = updateMarketInteractor;
        this.tradeInteractor = tradeInteractor;
        this.tradingPresenter = tradingPresenter;
    }

    /**
     * Executes a timer-triggered market update tick.
     */
    public void executeTimerTick() {
        updateMarketInteractor.executeExecuteTick();
    }

    /**
     * Executes a buy or sell trade request.
     *
     * @param ticker the ticker symbol being traded
     * @param amount the monetary amount the user wishes to trade
     * @param isBuy true for buy, false for sell
     * @param currentPrice the current market price
     */
    public void executeTrade(String ticker, double amount, boolean isBuy, double currentPrice) {
        final SimulatedTradeInputData inputData = new SimulatedTradeInputData(
                ticker,
                isBuy,
                amount,
                currentPrice
        );
        tradeInteractor.executeTrade(inputData);
    }

    /**
     * Returns the user to the previous view.
     */
    public void executeGoBack() {
        tradingPresenter.prepareGoBackView();
    }
}
