package interfaceadapter.simulated_trading;

import usecase.simulated_trade.SimulatedTradeInputBoundary;
import usecase.simulated_trade.SimulatedTradeInputData;
import usecase.update_market.UpdateMarketInputBoundary;

// ⭐ ADDED
import dataaccess.InMemorySessionDataAccessObject;

public class TradingController {

    private final UpdateMarketInputBoundary updateMarketInteractor;
    private final SimulatedTradeInputBoundary tradeInteractor;
    // ✅ Existing: must hold presenter to support "Back" button
    private final TradingPresenter tradingPresenter;

    // ⭐ Store the current logged-in user's session
    private final InMemorySessionDataAccessObject sessionDAO;

    // ⭐ UPDATED: added InMemorySessionDataAccessObject to the constructor
    public TradingController(UpdateMarketInputBoundary updateMarketInteractor,
                             SimulatedTradeInputBoundary tradeInteractor,
                             TradingPresenter tradingPresenter,
                             InMemorySessionDataAccessObject sessionDAO) {
        this.updateMarketInteractor = updateMarketInteractor;
        this.tradeInteractor = tradeInteractor;
        this.tradingPresenter = tradingPresenter;
        this.sessionDAO = sessionDAO;
    }

    // Triggered by the View's Timer (e.g., every second)
    public void executeTimerTick() {
        updateMarketInteractor.executeExecuteTick();
    }

    // Triggered by Buy/Sell buttons
    public void executeTrade(String ticker, double amount, boolean isBuy, double currentPrice) {
        SimulatedTradeInputData inputData = new SimulatedTradeInputData(
                ticker,
                isBuy,
                amount,
                currentPrice
        );
        tradeInteractor.executeTrade(inputData);
    }

    // ✅ Handle "Back" button click
    public void executeGoBack() {
        tradingPresenter.prepareGoBackView();
    }

    // ⭐ Used by “View All Order History” button
    public void executeOpenPortfolioSummary() {
        java.util.UUID userId = sessionDAO.getCurrentUserId();
        if (userId == null) {
            System.err.println("No logged-in user; cannot open summary.");
            return;
        }

        // Open the summary window, reusing the existing session
        app.PortfolioSummaryMain.show(userId, sessionDAO);
    }

    // Optional: allow other classes to access the session
    public InMemorySessionDataAccessObject getSessionDAO() {
        return sessionDAO;
    }
}