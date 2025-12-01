package interfaceadapter.simulated_trading;

import dataaccess.InMemorySessionDataAccessObject;
import usecase.simulated_trade.SimulatedTradeInputBoundary;
import usecase.simulated_trade.SimulatedTradeInputData;
import usecase.update_market.UpdateMarketInputBoundary;

/**
 * Controller for the simulated trading UI.
 * Handles user actions such as buy/sell, timer ticks, and view navigation.
 */
public class TradingController {

    /** Interactor for updating market prices. */
    private final UpdateMarketInputBoundary updateMarketInteractor;

    /** Interactor for executing user trades. */
    private final SimulatedTradeInputBoundary tradeInteractor;

    /** Presenter for navigating between views. */
    private final TradingPresenter tradingPresenter;

    /** Current logged-in user session. */
    private final InMemorySessionDataAccessObject sessionDAO;

    /**
     * Constructs a TradingController.
     *
     * @param updateMarketInteractor market update interactor
     * @param tradeInteractor        trade execution interactor
     * @param tradingPresenter       presenter handling UI updates
     * @param sessionDAO             user's session data access
     */
    public TradingController(UpdateMarketInputBoundary updateMarketInteractor,
                             SimulatedTradeInputBoundary tradeInteractor,
                             TradingPresenter tradingPresenter,
                             InMemorySessionDataAccessObject sessionDAO) {
        this.updateMarketInteractor = updateMarketInteractor;
        this.tradeInteractor = tradeInteractor;
        this.tradingPresenter = tradingPresenter;
        this.sessionDAO = sessionDAO;
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
     * Triggered by "Back" button to return to the setup view.
     */
    public void executeGoBack() {
        tradingPresenter.prepareGoBackView();
    }

    /**
     * Opens the Portfolio Summary page for the current user.
     * Triggered by "View All Order History" button.
     */
    public void executeOpenPortfolioSummary() {
        final java.util.UUID userId = sessionDAO.getCurrentUserId();

        if (userId != null) {
            app.PortfolioSummaryMain.show(userId, sessionDAO);
        }
        else {
            System.err.println("No logged-in user; cannot open summary.");
        }
    }

    /**
     * Returns the current user's session DAO.
     *
     * @return sessionDAO
     */
    public InMemorySessionDataAccessObject getSessionDAO() {
        return sessionDAO;
    }
}
