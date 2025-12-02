package interfaceadapter.simulated_trading;

import app.PortfolioSummaryMain;
import dataaccess.InMemorySessionDataAccessObject;
import usecase.simulated_trade.SimulatedTradeInputBoundary;
import usecase.simulated_trade.SimulatedTradeInputData;
import usecase.update_market.UpdateMarketInputBoundary;

import java.util.UUID;

/**
 * Controller for the simulated trading UI.
 * Handles user actions such as buy/sell, timer ticks, and view navigation.
 *
 * @param updateMarketInteractor Interactor for updating market prices.
 * @param tradeInteractor        Interactor for executing user trades.
 * @param sessionDAO             Current logged-in user session.
 */
public record TradingController(UpdateMarketInputBoundary updateMarketInteractor,
                                SimulatedTradeInputBoundary tradeInteractor,
                                InMemorySessionDataAccessObject sessionDAO) {

    /**
     * Constructs a TradingController.
     *
     * @param updateMarketInteractor market update interactor
     * @param tradeInteractor        trade execution interactor
     * @param sessionDAO             user's session data access
     */
    public TradingController {
    }

    /**
     * Triggered by UI timer (e.g., every second) to update market data.
     */
    public void executeTimerTick() {
        updateMarketInteractor.executeExecuteTick();
    }

    /**
     * Triggered by Buy/Sell buttons to place a trade.
     *
     * @param ticker       stock ticker
     * @param amount       number of shares
     * @param isBuy        true for buy, false for sell
     * @param currentPrice current market price
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
     * Opens the Portfolio Summary page for the current user.
     * Triggered by "View All Order History" button.
     */
    public void executeOpenPortfolioSummary() {
        final UUID userId = sessionDAO.getCurrentUserId();

        if (userId != null) {
            PortfolioSummaryMain.show(userId);
        } else {
            System.err.println("No logged-in user; cannot open summary.");
        }
    }

}
