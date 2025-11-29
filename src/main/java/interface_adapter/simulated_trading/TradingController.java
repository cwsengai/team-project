package interface_adapter.simulated_trading;

import use_case.simulated_trade.SimulatedTradeInputBoundary;
import use_case.simulated_trade.SimulatedTradeInputData;
import use_case.update_market.UpdateMarketInputBoundary;

// ⭐ ADDED
import dataaccess.InMemorySessionDataAccessObject;

public class TradingController {

    private final UpdateMarketInputBoundary updateMarketInteractor;
    private final SimulatedTradeInputBoundary tradeInteractor;
    // ✅ 原有：为了支持返回按钮，必须持有 Presenter
    private final TradingPresenter tradingPresenter;

    // ⭐ 保存当前登录用户的 Session
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

    // ✅ 处理返回按钮点击
    public void executeGoBack() {
        tradingPresenter.prepareGoBackView();
    }

    // ⭐ 用于“View All Order History”按钮
    public void executeOpenPortfolioSummary() {
        // Use the logged-in user from the session
        java.util.UUID userId = sessionDAO.getCurrentUserId();
        if (userId == null) {
            System.err.println("No logged-in user; cannot open summary.");
            return;
        }

        // Open the summary window, reusing the existing session
        app.PortfolioSummaryMain.show(userId, sessionDAO);
    }

    // 如果你还想从别的地方拿到 Session，可以保留这个 getter
    public InMemorySessionDataAccessObject getSessionDAO() {
        return sessionDAO;
    }
}
