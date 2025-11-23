package use_case.simulated_trade;

import java.time.LocalDateTime;
import java.util.UUID;

import entity.Account;
import entity.SimulatedTradeRecord;
import use_case.session.SessionDataAccessInterface;

public class SimulatedTradeInteractor implements SimulatedTradeInputBoundary {

    private final SimulatedTradeOutputBoundary presenter;
    private final Account account;
    private final SimulatedTradeDataAccessInterface dataAccess;
    private final SessionDataAccessInterface sessionDataAccess;

    public SimulatedTradeInteractor(SimulatedTradeOutputBoundary presenter, Account account,
                                    SimulatedTradeDataAccessInterface dataAccess, SessionDataAccessInterface sessionDataAccess) {
        this.presenter = presenter;
        this.account = account;
        this.dataAccess = dataAccess;
        this.sessionDataAccess = sessionDataAccess;
    }

    @Override
    public void executeTrade(SimulatedTradeInputData input) {
        String ticker = input.getTicker();
        double price = input.getCurrentPrice();
        double amountUSD = input.getAmount();
        boolean isBuy = input.isBuyAction();

        LocalDateTime tradeTime = LocalDateTime.now();

        // 1. Validate input amount
        if (amountUSD <= 0) {
            presenter.prepareFailView("Amount must be positive.");
            return;
        }

        // 2. Calculate share quantity
        int quantity = (int) (amountUSD / price);

        if (quantity <= 0) {
            presenter.prepareFailView("Amount too low to buy 1 share.");
            return;
        }

        // 3. Check for sufficient funds
        double totalCost = quantity * price;
        if (isBuy && account.getBalance() < totalCost) {
            presenter.prepareFailView("Insufficient funds.");
            return;
        }

        // 4. Execute trade logic in Entity
        account.executeTrade(ticker, isBuy, quantity, price, tradeTime);

        // 5. Save trade record with user ID (session-aware)
        UUID currentUserId = sessionDataAccess.getCurrentUserId();
        // For simplicity, treat this as a closed trade for persistence
        SimulatedTradeRecord trade = new SimulatedTradeRecord(
                ticker,
                isBuy, // direction
                quantity,
                price, // entry price
                price, // exit price (same as entry for instant trade)
                0.0,   // realizedPnL (set to 0, or calculate if needed)
                tradeTime,
                tradeTime
        );
        dataAccess.saveTrade(trade, currentUserId);

        // 6. Prepare success response
        String action = isBuy ? "Bought" : "Sold (Short)";
        String message = String.format("Successfully %s %d shares of %s at $%.2f",
                action, quantity, ticker, price);

        // 7. Notify Presenter
        SimulatedTradeOutputData outputData = new SimulatedTradeOutputData(account.getBalance(), message);
        presenter.prepareSuccessView(outputData);
    }
}